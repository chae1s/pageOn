package com.pageon.backend.service;

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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@Transactional
@ActiveProfiles("test")
@DisplayName("PortOneService 단위 테스트")
@ExtendWith(MockitoExtension.class)
class PortOneServiceTest {
    @InjectMocks
    private PortOneService portOneService;
    @Mock
    private RedisTemplate<String, Object> redisTemplate;
    @Mock
    private ValueOperations<String, Object> valueOperations;
    @Mock
    private UserRepository userRepository;
    @Mock
    SmsService smsService;
    private PrincipalUser mockPrincipalUser;
    @Mock
    private CommonService commonService;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
        mockPrincipalUser = mock(PrincipalUser.class);
    }

    @Test
    @DisplayName("로그인한 유저가 본인인증 하기 버튼을 눌렀을 때 생성된 identityVerificationId가 null이 아니다.")
    void whenAuthenticatedUserInitiatesVerification_thenIdentityVerificationIdIsGenerated() {
        // given
        String email = "test@mail.com";
        User user = User.builder()
                .id(1L)
                .email(email)
                .password("password")
                .deleted(false)
                .build();
        when(commonService.findUserByEmail(mockPrincipalUser.getUsername())).thenReturn(user);
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);

        //when
        IdentityVerificationIdResponse response = portOneService.createIdentityVerificationId(mockPrincipalUser);

        // then
        assertNotNull(response.getIdentityVerificationId());

    }

    @Test
    @DisplayName("로그인한 유저가 이미 본인인증을 완료했을 경우 CustomException 발생")
    void createIdentityVerificationId_whenIsPhoneVerifiedTrue_shouldThrowCustomException() {
        // given
        User user = User.builder()
                .id(1L)
                .email("test@mail.com")
                .password("password")
                .deleted(false)
                .isPhoneVerified(true)
                .build();

        when(commonService.findUserByEmail(mockPrincipalUser.getUsername())).thenReturn(user);
        when(userRepository.existsByEmailAndIsPhoneVerifiedTrue(mockPrincipalUser.getUsername())).thenReturn(true);

        //when
        CustomException exception = assertThrows(CustomException.class, () -> {
            portOneService.createIdentityVerificationId(mockPrincipalUser);
        });

        // then
        assertEquals("이미 본인인증을 완료한 사용자입니다.", exception.getErrorMessage());
        assertEquals(ErrorCode.IDENTITY_ALREADY_VERIFIED, ErrorCode.valueOf(exception.getErrorCode()));
    }
    
    @Test
    @DisplayName("method가 SMS인 경우 로그인한 유저가 본인인증에 유효한 정보를 입력했을 때 otp번호를 생성 후 redis에 저장")
    void createAndStoreOtp_withValidUserAndValidInput_shouldCreateOtpAndSaveRedis() {
        // given
        String email = "test@mail.com";
        String expectedId = "identity-verification-id";
        User user = User.builder()
                .id(1L)
                .email(email)
                .password("password")
                .deleted(false)
                .build();
        // 생성된 id를 redis에서 가져온다.
        String redisKey = String.format("%s_identityVerificationId", user.getEmail());
        when(commonService.findUserByEmail(mockPrincipalUser.getUsername())).thenReturn(user);
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get(redisKey)).thenReturn(expectedId);

        doReturn(true).when(smsService).singleMessageSend(any(OtpVerificationPayload.class));

        // portOneService의 createAndStoreOtp에서 사용할 request
        IdentityVerificationCustomer customer = new IdentityVerificationCustomer("박누구", "010-1111-1111", "9604032");
        IdentityVerificationRequest request = new IdentityVerificationRequest(customer, "SMS");

        // 6자리 인증번호 캡쳐 
        ArgumentCaptor<OtpVerificationPayload> otpCaptor = ArgumentCaptor.forClass(OtpVerificationPayload.class);
        
        //when
        portOneService.createAndStoreOtp(expectedId, mockPrincipalUser, request);
        
        // then
        // Redis에서 id를 가져왔는지 확인
        verify(valueOperations).get(redisKey);
        verify(valueOperations).set(eq(expectedId), otpCaptor.capture(), eq(Duration.ofMinutes(3)));

        OtpVerificationPayload otpPayload = otpCaptor.getValue();
        assertNotNull(otpPayload.getOtp());
        assertEquals(6, otpPayload.getOtp().length());
        assertTrue(otpPayload.getOtp().matches("\\d{6}"));
    }

    @Test
    @DisplayName("입력한 전화번호가 이미 인증이 완료된 상태라면 CustomException 발생")
    void createAndStoreOtp_whenAlreadyVerifiedPhoneNumber_shouldThrowCustomException() {
        // given
        String identityVerificationId = "somdId";
        String phoneNumber = "01011111111";
        User user = User.builder()
                .id(1L)
                .email("test@mail.com")
                .password("password")
                .deleted(false)
                .phoneNumber(phoneNumber)
                .isPhoneVerified(true)
                .build();

        IdentityVerificationCustomer customer = new IdentityVerificationCustomer("박누구", phoneNumber, "9604032");
        IdentityVerificationRequest request = new IdentityVerificationRequest(customer, "SMS");

        when(commonService.findUserByEmail(mockPrincipalUser.getUsername())).thenReturn(user);
        when(userRepository.existsByPhoneNumberAndIsPhoneVerifiedTrue(phoneNumber)).thenReturn(true);
        //when
        CustomException exception = assertThrows(CustomException.class, () -> {
            portOneService.createAndStoreOtp(identityVerificationId, mockPrincipalUser, request);
        });

        // then
        assertEquals("해당 전화번호는 이미 본인인증에 사용되었습니다.", exception.getErrorMessage());
        assertEquals(ErrorCode.PHONE_NUMBER_ALREADY_VERIFIED, ErrorCode.valueOf(exception.getErrorCode()));


    }
    
    @Test
    @DisplayName("전달받은 method가 SMS이 아닌 경우 CustomException 발생")
    void createAndStoreOtp_withInvalidMethod_shouldThrowException() {
        // given
        String email = "test@mail.com";
        String expectedId = "identity-verification-id";
        User user = User.builder()
                .id(1L)
                .email(email)
                .password("password")
                .deleted(false)
                .build();

        when(commonService.findUserByEmail(mockPrincipalUser.getUsername())).thenReturn(user);;

        IdentityVerificationCustomer customer = new IdentityVerificationCustomer("박누구", "010-1111-1111", "9604032");
        IdentityVerificationRequest request = new IdentityVerificationRequest(customer, "NULL");
        
        //when
        CustomException exception = assertThrows(CustomException.class, () -> {
           portOneService.createAndStoreOtp(expectedId, mockPrincipalUser, request); 
        });
        
        
        // then
        assertEquals("지원하지 않는 본인인증 방식입니다.", exception.getErrorMessage());
        assertEquals(ErrorCode.INVALID_VERIFICATION_METHOD, ErrorCode.valueOf(exception.getErrorCode()));
    }
    
    @Test
    @DisplayName("전달받은 identityVerificationId와 redis에 저장된 identityVerificationId가 다르면 CustomException 발생")
    void createAndStoreOtp_withMissmatchIdentityVerificationId_shouldThrowCustomException() {
        // given
        String email = "test@mail.com";
        String id = "identity-verification-id";
        String redisId = "redis-identity-verification-id";

        User user = User.builder()
                .id(1L)
                .email(email)
                .password("password")
                .deleted(false)
                .build();

        when(commonService.findUserByEmail(mockPrincipalUser.getUsername())).thenReturn(user);
        String redisKey = String.format("%s_identityVerificationId", user.getEmail());

        IdentityVerificationCustomer customer = new IdentityVerificationCustomer("박누구", "010-1111-1111", "9604032");
        IdentityVerificationRequest request = new IdentityVerificationRequest(customer, "SMS");

        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get(redisKey)).thenReturn(redisId);

        //when
        CustomException exception = assertThrows(CustomException.class, () -> {
            portOneService.createAndStoreOtp(id, mockPrincipalUser, request);
        });
        
        // then
        assertEquals("전달된 인증 ID가 일치하지 않습니다.", exception.getErrorMessage());
        assertEquals(ErrorCode.IDENTITY_VERIFICATION_ID_NOT_MATCH, ErrorCode.valueOf(exception.getErrorCode()));
    }
    
    @Test
    @DisplayName("받아온 otp가 일치할 때 user DB Update")
    void verifyOtpAndUpdateUser_withMatchOtp_shouldUpdateUser() {
        // given
        // 프론트에서 온 otp 번호
        String sendOtp = "493029";
        String identityVerificationId = "identity-verification-id";
        String redisOtp = "493029";

        User user = User.builder()
                .id(1L)
                .email("test@mail.com")
                .password("password")
                .deleted(false)
                .build();

        OtpVerificationPayload payload = new OtpVerificationPayload(redisOtp, new IdentityVerificationCustomer("박누구", "01011111111", "9504022"));
        String redisKey = user.getEmail() + "_identityVerificationId";
        when(commonService.findUserByEmail(mockPrincipalUser.getUsername())).thenReturn(user);
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get(redisKey)).thenReturn(identityVerificationId);
        when(valueOperations.get(identityVerificationId)).thenReturn(payload);

        String di = UUID.randomUUID().toString();
        IdentityVerificationResultRequest request = new IdentityVerificationResultRequest(di, sendOtp, "DANAL");
        //when
        portOneService.verifyOtpAndUpdateUser(identityVerificationId, mockPrincipalUser, request);

        // then
        assertEquals("박누구", user.getName());
        assertEquals("01011111111", user.getPhoneNumber());
        assertTrue(user.getIsPhoneVerified());
        
        
    }

    
    @Test
    @DisplayName("identityVerificationId로 redis에서 꺼낸 데이터가 없을 경우 CustomException 발생")
    void verifyOtpAndUpdateUser_withNullStoredData_shouldThrowCustomException() {
        // given
        String sendOtp = "493029";
        String identityVerificationId = "identity-verification-id";
        String redisOtp = "493029";

        User user = User.builder()
                .id(1L)
                .email("test@mail.com")
                .password("password")
                .deleted(false)
                .build();

        String redisKey = user.getEmail() + "_identityVerificationId";
        when(commonService.findUserByEmail(mockPrincipalUser.getUsername())).thenReturn(user);
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get(redisKey)).thenReturn(identityVerificationId);
        when(valueOperations.get(identityVerificationId)).thenReturn(null);
        IdentityVerificationResultRequest request = new IdentityVerificationResultRequest();
        //when
        CustomException exception = assertThrows(CustomException.class, () -> {
            portOneService.verifyOtpAndUpdateUser(identityVerificationId, mockPrincipalUser, request);
        });
        
        // then
        assertEquals("OTP 정보가 존재하지 않거나 만료되었습니다.", exception.getErrorMessage());
        assertEquals(ErrorCode.OTP_PAYLOAD_NOT_FOUND, ErrorCode.valueOf(exception.getErrorCode()));
        
    }

    @Test
    @DisplayName("프론트에서 받은 otp 번호가 redis에 저장된 otp 번호와 일치하지 않을 때 CustomException 발생")
    void verifyOtpAndUpdateUser_withMissmatchOtp_shouldThrowCustomException() {
        // given
        String sendOtp = "392059";
        String identityVerificationId = "identity-verification-id";
        String redisOtp = "493029";


        User user = User.builder()
                .id(1L)
                .email("test@mail.com")
                .password("password")
                .deleted(false)
                .build();

        String redisKey = user.getEmail() + "_identityVerificationId";
        OtpVerificationPayload payload = new OtpVerificationPayload(redisOtp, new IdentityVerificationCustomer("박누구", "01011111111", "9504022"));

        when(commonService.findUserByEmail(mockPrincipalUser.getUsername())).thenReturn(user);
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get(redisKey)).thenReturn(identityVerificationId);
        when(valueOperations.get(identityVerificationId)).thenReturn(payload);

        String di = UUID.randomUUID().toString();
        IdentityVerificationResultRequest request = new IdentityVerificationResultRequest(di, sendOtp, "DANAL");

        //when
        CustomException exception = assertThrows(CustomException.class, () -> {
            portOneService.verifyOtpAndUpdateUser(identityVerificationId, mockPrincipalUser, request);
        });

        // then
        assertEquals("전달된 OTP가 일치하지 않습니다.", exception.getErrorMessage());
        assertEquals(ErrorCode.OTP_NOT_MATCH, ErrorCode.valueOf(exception.getErrorCode()));

    }


}