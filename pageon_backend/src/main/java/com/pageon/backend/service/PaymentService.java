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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
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

    @Value("${payment.secret.key}")
    private String secretKey;

    @Transactional
    public PaymentResponse.Ready readyPayment(Long userId, PaymentRequest.Ready request) {
        User user = userRepository.findById(userId).orElseThrow(
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
                .description(request.getDescription())
                .orderId(orderId)
                .build();

        pointTransactionRepository.save(pointTransaction);

        return PaymentResponse.Ready.builder()
                .orderId(orderId)
                .customerKey(user.getCustomerKey())
                .amount(request.getAmount())
                .build();
    }

    @Transactional
    public PaymentResponse.Result confirmPayment(Long userId, PaymentRequest.Confirm confirm) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new CustomException(ErrorCode.USER_NOT_FOUND)
        );

        PointTransaction transaction = pointTransactionRepository.findByUser_IdAndOrderId(userId, confirm.getOrderId()).orElseThrow(
                () -> new CustomException(ErrorCode.POINT_TRANSACTION_NOT_FOUND)
        );

        if (transaction.getTransactionStatus() != TransactionStatus.PENDING) {
            return PaymentResponse.Result.builder()
                    .success(false)
                    .message(ErrorCode.ALREADY_PAYMENT_CONFIRM.getErrorMessage())
                    .build();
        }

        if (!transaction.getAmount().equals(confirm.getAmount())) {
            transaction.failedCharge();
            return PaymentResponse.Result.builder()
                    .success(false)
                    .message(ErrorCode.AMOUNT_NOT_MATCH.getErrorMessage())
                    .build();
        }

        PaymentResponse.ConnectionData data = createConnection(transaction, confirm);
        Map<String, Object> result = data.getResult();

        String paidAtStr = result.get("approvedAt").toString();
        LocalDateTime paidAt = OffsetDateTime.parse(paidAtStr).toLocalDateTime();

        user.chargePoints(confirm.getAmount());

        transaction.completedCharge(paidAt, user.getPointBalance());

        return PaymentResponse.Result
                .builder()
                .success(data.getSuccess())
                .message(data.getMessage())
                .build();

    }

    private String requestToJson(PaymentRequest.Confirm confirm) {
        try {
            return objectMapper.writeValueAsString(confirm);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    private PaymentResponse.ConnectionData createConnection(PointTransaction transaction, PaymentRequest.Confirm confirm) {
        try {
            String jsonBody = requestToJson(confirm);
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://api.tosspayments.com/v1/payments/confirm"))
                    .header("Authorization", "Basic " + secretKey)
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                    .build();
            HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());

            return PaymentResponse.ConnectionData.builder()
                    .success(true)
                    .result(jsonToMap(response.body()))
                    .message("결제가 완료되었습니다.")
                    .build();

        } catch (Exception e) {

            transaction.failedCharge();
            return PaymentResponse.ConnectionData.builder()
                    .success(false)
                    .result(null)
                    .message("결제에 실패하였습니다.")
                    .build();
        }
    }

    private Map<String, Object> jsonToMap(String response) {
        try {
            return objectMapper.readValue(response, new TypeReference<Map<String, Object>>() {});
        } catch (Exception e) {
            log.error("json to map exception", e);
            throw new RuntimeException(e);
        }
    }
}
