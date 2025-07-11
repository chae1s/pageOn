package com.pageon.backend.service;

import com.pageon.backend.common.enums.Gender;
import com.pageon.backend.common.enums.IdentityProvider;
import com.pageon.backend.dto.common.IdentityVerificationCustomer;
import com.pageon.backend.dto.payload.OtpVerificationPayload;
import com.pageon.backend.dto.request.IdentityVerificationRequest;
import com.pageon.backend.dto.request.IdentityVerificationResultRequest;
import com.pageon.backend.dto.response.IdentityVerificationIdResponse;
import com.pageon.backend.entity.User;
import com.pageon.backend.exception.CustomException;
import com.pageon.backend.exception.ErrorCode;
import com.pageon.backend.repository.UserRepository;
import com.pageon.backend.security.PrincipalUser;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class PortOneService {

    private final UserRepository userRepository;
    private final RedisTemplate<String, Object> redisTemplate;
    private final SmsService smsService;

    //  본인인증 요청 식별 위해 identityVerificationId 발급 후 redis에 저장
    public IdentityVerificationIdResponse createIdentityVerificationId(PrincipalUser principalUser) {
        User user = userRepository.findByEmailAndIsDeletedFalse(principalUser.getUsername()).orElseThrow(
                () -> new CustomException(ErrorCode.USER_NOT_FOUND)
        );

        if (userRepository.existsByEmailAndIsPhoneVerifiedTrue(principalUser.getUsername()))
            throw new CustomException(ErrorCode.IDENTITY_ALREADY_VERIFIED);

        String identityVerificationId = UUID.randomUUID().toString();
        try {
            redisTemplate.opsForValue().set(String.format("%s_identityVerificationId", user.getEmail()), identityVerificationId, Duration.ofMinutes(10));
        } catch (Exception e) {
            throw new CustomException(ErrorCode.REDIS_CONNECTION_FAILED);
        }

        return new IdentityVerificationIdResponse(identityVerificationId);
    }

    public boolean createAndStoreOtp(String identityVerificationId, PrincipalUser principalUser, IdentityVerificationRequest identityVerificationRequest) {
        // 로그인한 유저의 이메일로 db 검색
        User user = userRepository.findByEmailAndIsDeletedFalse(principalUser.getUsername()).orElseThrow(
                () -> new CustomException(ErrorCode.USER_NOT_FOUND)
        );

        if (!identityVerificationRequest.getMethod().equals("SMS"))
            throw new CustomException(ErrorCode.INVALID_VERIFICATION_METHOD);

        if (userRepository.existsByPhoneNumberAndIsPhoneVerifiedTrue(identityVerificationRequest.getCustomer().getPhoneNumber()))
            throw new CustomException(ErrorCode.PHONE_NUMBER_ALREADY_VERIFIED);

        checkIdentityVerificationId(identityVerificationId, user.getEmail());

        Random random = new Random();
        String otp = String.valueOf(random.nextInt(900000) + 100000);

        OtpVerificationPayload otpVerificationPayload = new OtpVerificationPayload(otp, identityVerificationRequest.getCustomer());

        try {
            redisTemplate.opsForValue().set(identityVerificationId, otpVerificationPayload, Duration.ofMinutes(3));
        } catch (Exception e) {
            throw new CustomException(ErrorCode.REDIS_CONNECTION_FAILED);
        }

        // 문자 전송 메소드 추가
        return smsService.singleMessageSend(otpVerificationPayload);

    }

    @Transactional
    public boolean verifyOtpAndUpdateUser(String identityVerificationId, PrincipalUser principalUser, IdentityVerificationResultRequest request) {
        User user = userRepository.findByEmailAndIsDeletedFalse(principalUser.getUsername()).orElseThrow(
                () -> new CustomException(ErrorCode.USER_NOT_FOUND)
        );

        checkIdentityVerificationId(identityVerificationId, user.getEmail());

        OtpVerificationPayload otpVerificationPayload;
        try {
            otpVerificationPayload = (OtpVerificationPayload) redisTemplate.opsForValue().get(identityVerificationId);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new CustomException(ErrorCode.REDIS_CONNECTION_FAILED);
        }

        if (otpVerificationPayload == null)
            throw new CustomException(ErrorCode.OTP_PAYLOAD_NOT_FOUND);

        if (!request.getOtp().equals(otpVerificationPayload.getOtp()))
            throw new CustomException(ErrorCode.OTP_NOT_MATCH);

        IdentityVerificationCustomer customer = otpVerificationPayload.getCustomer();

        Map<String, Object> identityNumInfo = parseIdentityNumber(customer.getIdentityNumber());


        user.updateIdentityVerification(
                customer.getName(), customer.getPhoneNumber(),
                (LocalDate) identityNumInfo.get("birthDate"), Gender.valueOf((String) identityNumInfo.get("gender")),
                request.getDi(), true, IdentityProvider.valueOf(request.getIdentityProvider())
        );

        try {
            redisTemplate.delete(identityVerificationId);
            redisTemplate.delete(String.format("%s_identityVerificationId", user.getEmail()));
        } catch (Exception e) {
            throw new CustomException(ErrorCode.REDIS_CONNECTION_FAILED);
        }

        return true;
    }

    // redis에 저장된 identityVerificationId와 url로 넘어온 identityVerificationId를 비교하는 메서드
    private void checkIdentityVerificationId(String identityVerificationId, String email) {
        try {
            String storedVerificationId = (String) redisTemplate.opsForValue().get(String.format("%s_identityVerificationId", email));

            if (!identityVerificationId.equals(storedVerificationId))
                throw new CustomException(ErrorCode.IDENTITY_VERIFICATION_ID_NOT_MATCH);

        } catch (Exception e) {
            log.error(e.getMessage());
            throw new CustomException(ErrorCode.REDIS_CONNECTION_FAILED);
        }
    }

    // 주민번호 앞자리 6 + 뒷자리 1 인 identityNumber를 이용해 생년월일과 성별을 구하는 메소드
    private Map<String, Object> parseIdentityNumber(String identityNumber) {
        String birthDate = identityNumber.substring(0, 6);
        int genderNum = Integer.parseInt(identityNumber.substring(6));

        String yearPart = birthDate.substring(0, 2);
        String monthPart = birthDate.substring(2, 4);
        String dayPart = birthDate.substring(4);

        int year = Integer.parseInt(yearPart);
        int currentYear = LocalDate.now().getYear() % 100;

        int fullYear = (year <= currentYear) ? 2000 + year : 1900 + year;

        String gender = (genderNum % 2 == 0) ? "FEMALE" : "MALE";

        return Map.of(
                "birthDate", LocalDate.of(fullYear, Integer.parseInt(monthPart), Integer.parseInt(dayPart)),
                "gender", gender
        );

    }
}
