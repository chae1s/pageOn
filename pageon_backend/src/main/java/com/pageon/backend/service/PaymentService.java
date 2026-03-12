package com.pageon.backend.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pageon.backend.common.enums.TransactionStatus;
import com.pageon.backend.common.enums.TransactionType;
import com.pageon.backend.dto.request.PaymentRequest;
import com.pageon.backend.dto.response.PaymentResponse;
import com.pageon.backend.entity.PointTransaction;
import com.pageon.backend.entity.User;
import com.pageon.backend.exception.CustomException;
import com.pageon.backend.exception.ErrorCode;
import com.pageon.backend.repository.PointTransactionRepository;
import com.pageon.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentService {
    private final UserRepository userRepository;
    private final PointTransactionRepository pointTransactionRepository;
    private final ObjectMapper objectMapper;
    private final IdempotentService idempotentService;

    @Value("${payment.secret.key}")
    private String secretKey;
    private RedisTemplate<String, PointTransaction> redisTemplate;

    @Transactional
    public PaymentResponse.Ready readyPayment(Long userId, PaymentRequest.Ready request) {

        String[] key = {String.valueOf(userId), "ready",request.getAmount().toString()};
        idempotentService.isValidIdempotent(Arrays.asList(key));

        User user = userRepository.findByIdWithLock(userId).orElseThrow(
                () -> new CustomException(ErrorCode.USER_NOT_FOUND)
        );

        if (user.getCustomerKey() == null) {
            String customerKey = "CUSK_" + UUID.randomUUID().toString().replace("-", "").substring(0, 12);
            user.assignCustomerKey(customerKey);
        }

        String orderId = "ORD_" + UUID.randomUUID().toString().substring(0, 12);

        PointTransaction pointTransaction = PointTransaction.builder()
                .user(user)
                .transactionType(TransactionType.CHARGE)
                .transactionStatus(TransactionStatus.PENDING)
                .amount(request.getAmount())
                .point(request.getPoint())
                .description(request.getDescription())
                .orderId(orderId)
                .build();

        String redisKey = String.format("point:payment:%d:%s", userId, orderId);
        redisTemplate.opsForValue().set(redisKey, pointTransaction);

        return PaymentResponse.Ready.builder()
                .orderId(orderId)
                .customerKey(user.getCustomerKey())
                .amount(request.getAmount())
                .build();
    }

    @Transactional
    public void confirmPayment(Long userId, PaymentRequest.Confirm confirm) {

        String[] key = {String.valueOf(userId), confirm.getOrderId(), confirm.getAmount().toString()};
        idempotentService.isValidIdempotent(Arrays.asList(key));

        User user = userRepository.findByIdWithLock(userId).orElseThrow(
                () -> new CustomException(ErrorCode.USER_NOT_FOUND)
        );

        String redisKey = String.format("point:payment:%d:%s", userId, confirm.getOrderId());
        PointTransaction transaction = redisTemplate.opsForValue().getAndDelete(redisKey);
        if (transaction == null) {
            throw new CustomException(ErrorCode.POINT_TRANSACTION_NOT_FOUND);
        }

        if (transaction.getTransactionStatus() != TransactionStatus.PENDING) {
            throw new CustomException(ErrorCode.ALREADY_PAYMENT_CONFIRM);
        }

        if (!transaction.getAmount().equals(confirm.getAmount())) {
            transaction.failedPayment();
            throw new CustomException(ErrorCode.AMOUNT_NOT_MATCH);
        }

        Map<String, Object> result = confirmConnection(transaction, confirm);

        String paidAtStr = result.get("approvedAt").toString();
        LocalDateTime paidAt = OffsetDateTime.parse(paidAtStr).toLocalDateTime();

        String paymentMethod = formatMethod(result);

        user.changePoints(confirm.getAmount());

        transaction.completedPayment(paidAt, user.getPointBalance(), confirm.getPaymentKey(), paymentMethod);

        pointTransactionRepository.save(transaction);

    }

    private String requestToJson(PaymentRequest.Confirm confirm) {
        try {
            return objectMapper.writeValueAsString(confirm);
        } catch (JsonProcessingException e) {
            throw new CustomException(ErrorCode.JSON_PARSE_FAILED);
        }
    }

    private Map<String, Object> confirmConnection(PointTransaction transaction, PaymentRequest.Confirm confirm) {
        try {
            String jsonBody = requestToJson(confirm);
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://api.tosspayments.com/v1/payments/confirm"))
                    .header("Authorization", "Basic " + secretKey)
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                    .build();
            HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());

            return jsonToMap(response.body());

        } catch (Exception e) {
            transaction.failedPayment();
            throw new CustomException(ErrorCode.PAYMENT_FAILED);
        }
    }

    private Map<String, Object> jsonToMap(String response) {
        try {
            return objectMapper.readValue(response, new TypeReference<Map<String, Object>>() {});
        } catch (Exception e) {
            log.error("json to map exception", e);
            throw new CustomException(ErrorCode.JSON_PARSE_FAILED);
        }
    }

    private String formatMethod(Map<String, Object> result) {

        String method = result.get("method").toString();

        @SuppressWarnings("unchecked")
        Map<String, Object> easyPay = (Map<String, Object>) result.get("easyPay");
        if (easyPay != null) {
            return String.format("%s %s", method, easyPay.get("provider").toString());
        } else {
            return method;
        }
    }

    @Transactional
    public String cancelPayment(Long userId, Long transactionId) {

        String[] key = {String.valueOf(userId), "cancel",transactionId.toString()};
        idempotentService.isValidIdempotent(Arrays.asList(key));

        User user = userRepository.findById(userId).orElseThrow(
                () -> new CustomException(ErrorCode.USER_NOT_FOUND)
        );

        PointTransaction transaction = pointTransactionRepository.findByIdAndUserWithLock(transactionId, userId).orElseThrow(
                () -> new CustomException(ErrorCode.POINT_TRANSACTION_NOT_FOUND)
        );

        if (user.getPointBalance() <= transaction.getAmount()) {
            return "환불 가능한 포인트가 없습니다.";
        }

        LocalDateTime limitDate = LocalDateTime.now().minusDays(7);
        if (transaction.getPaidAt().isBefore(limitDate)) {
            return "환불 기한이 지났습니다.";
        }

        String url = String.format("https://api.tosspayments.com/v1/payments/%s/cancel", transaction.getPaymentKey());

        Map<String, String> result = cancelConnection(url);

        LocalDateTime cancelledAt = OffsetDateTime.parse(result.get("cancelledAt")).toLocalDateTime();

        user.changePoints(-transaction.getAmount());
        transaction.cancelPayment(user.getPointBalance(), cancelledAt);

        return result.get("message");
    }

    private Map<String, String> cancelConnection(String url) {

        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Authorization", "Basic " + secretKey)
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString("{\"cancelReason\":\"구매자가 취소를 원함\"}"))
                    .build();
            HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());

            Map<String, Object> result = jsonToMap(response.body());
            String cancelledAtStr = result.get("approvedAt").toString();

            log.info("cancel connection response: {}", response.body());

            return Map.of(
                    "message", "환불이 완료되었습니다.",
                    "cancelledAt", cancelledAtStr
            );
        } catch (Exception e) {
            return Map.of("message", "환불에 실패하였습니다.");
        }

    }
}
