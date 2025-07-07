package com.pageon.backend.service;

import com.pageon.backend.dto.request.FindPasswordRequest;
import com.pageon.backend.dto.request.LoginRequest;
import com.pageon.backend.dto.request.SignupRequest;
import com.pageon.backend.dto.request.UserUpdateRequest;
import com.pageon.backend.dto.response.JwtTokenResponse;
import com.pageon.backend.dto.response.UserInfoResponse;
import com.pageon.backend.dto.token.AccessToken;
import com.pageon.backend.dto.token.TokenInfo;
import com.pageon.backend.entity.Role;
import com.pageon.backend.entity.UserRole;
import com.pageon.backend.entity.Users;
import com.pageon.backend.common.enums.Provider;
import com.pageon.backend.common.enums.RoleType;
import com.pageon.backend.exception.CustomException;
import com.pageon.backend.exception.ErrorCode;
import com.pageon.backend.repository.RoleRepository;
import com.pageon.backend.repository.UserRepository;
import com.pageon.backend.security.JwtProvider;
import com.pageon.backend.security.PrincipalUser;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
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
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.RestTemplate;

import static org.mockito.Mockito.*;


import java.time.LocalDate;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;


@Transactional
@ActiveProfiles("test")
@DisplayName("userService 단위 테스트")
@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @InjectMocks
    private UserService userService;
    @Mock
    private RoleService roleService;
    @Mock
    private UserRepository userRepository;
    @Mock
    private RoleRepository roleRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private JwtProvider jwtProvider;
    @Mock
    private RedisTemplate<String, Object> redisTemplate;
    @Mock
    private HttpServletResponse response;
    @Mock
    private HttpServletRequest request;
    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private MailService mailService;
    @Mock
    private RestTemplate restTemplate;
    private PrincipalUser mockPrincipalUser;
    @Mock
    private ValueOperations<String, Object> valueOperations;


    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
        roleRepository.save(new Role("ROLE_USER"));

        mockPrincipalUser = mock(PrincipalUser.class);
    }

    @Test
    @DisplayName("모든 정보가 유효할 때 회원가입 성공")
    void signup_withValidInfo_shouldSucceed() {
        // given
        SignupRequest signupRequest = new SignupRequest("test@mail.com", "!test1234", "nickname", "19950402");
        when(userRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        doNothing().when(roleService).assignDefaultRole(any(Users.class));
        when(passwordEncoder.encode(any())).thenReturn("encodePassword");

        ArgumentCaptor<Users> userCaptor = ArgumentCaptor.forClass(Users.class);

        //when
        userService.signup(signupRequest);
        
        // then

        verify(userRepository).save(userCaptor.capture());
        Users savedUser = userCaptor.getValue();

        assertEquals("test@mail.com", savedUser.getEmail());
        assertEquals("nickname", savedUser.getNickname());
        assertEquals("encodePassword", savedUser.getPassword());
        assertEquals(LocalDate.of(1995, 4, 2), savedUser.getBirthDate());
        assertFalse(savedUser.getIsDeleted());
        
    }

    @Test
    @DisplayName("ROLE_USER가 DB에 없을 경우 CustomException 발생")
    void signup_withoutRole_shouldThrowCustomException() {
        // given
        roleRepository.deleteAll();

        SignupRequest signupRequest = new SignupRequest("test@mail.com", "!test1234", "nickname", "19950402");

        doThrow(new CustomException(ErrorCode.ROLE_NOT_FOUND)).when(roleService).assignDefaultRole(any(Users.class));

        //when + then
        CustomException exception = assertThrows(CustomException.class, () -> {
            userService.signup(signupRequest);

        });

        assertEquals("기본 권한이 없습니다.", exception.getErrorMessage());
        assertEquals(ErrorCode.ROLE_NOT_FOUND, ErrorCode.valueOf(exception.getErrorCode()));

    }
    
    @Test
    @DisplayName("회원가입 시 기본 권한 UserRole 함께 저장")
    void signup_shouldCreateUserRoleWithDefaultRole() {
        // given
        SignupRequest signupRequest = new SignupRequest("test@mail.com", "!test1234", "nickname", "19950402");
        when(userRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        when(passwordEncoder.encode(any())).thenReturn("encodePassword");
        doAnswer(invocation -> {
            Users user = invocation.getArgument(0);
            Role dummyRole = Role.builder().roleType(RoleType.ROLE_USER).build();
            UserRole userRole = UserRole.builder().user(user).role(dummyRole).build();
            user.getUserRoles().add(userRole);
            return null;
        }).when(roleService).assignDefaultRole(any(Users.class));

        ArgumentCaptor<Users> userCaptor = ArgumentCaptor.forClass(Users.class);


        //when
        userService.signup(signupRequest);
        
        // then
        verify(userRepository).save(userCaptor.capture());
        Users savedUser = userCaptor.getValue();

        assertFalse(savedUser.getUserRoles().isEmpty(), "userRole이 저장되지 않았습니다.");

        RoleType roleType = savedUser.getUserRoles().get(0).getRole().getRoleType();
        assertEquals(RoleType.ROLE_USER, roleType, "기본 권한이 ROLE_USER가 아닙니다.");
    }

    
    @Test
    @DisplayName("회원가입 시 provider는 EMAIL, providerId는 null로 저장")
    void signup_shouldSetProviderAsEmailAndProviderIdAsNull() {
        // given
        SignupRequest signupRequest = new SignupRequest("test@mail.com", "!test1234", "nickname", "19950402");
        when(userRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        doNothing().when(roleService).assignDefaultRole(any(Users.class));
        when(passwordEncoder.encode(any())).thenReturn("encodePassword");

        ArgumentCaptor<Users> userCaptor = ArgumentCaptor.forClass(Users.class);
        //when
        userService.signup(signupRequest);
        
        // then
        verify(userRepository).save(userCaptor.capture());
        Users savedUser = userCaptor.getValue();

        assertEquals(savedUser.getProvider(), Provider.EMAIL, "Provider가 EMAIL입니다.");
        
        assertNull(savedUser.getProviderId(), "ProviderId가 null입니다.");
    }

    @Test
    @DisplayName("회원가입 시 IsDeleted는 false로 저장")
    void signup_shouldSetIsDeletedAsFalseByDefault() {
        SignupRequest signupRequest = new SignupRequest("test@mail.com", "!test1234", "nickname", "19950402");
        when(userRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        doNothing().when(roleService).assignDefaultRole(any(Users.class));
        when(passwordEncoder.encode(any())).thenReturn("encodePassword");

        ArgumentCaptor<Users> userCaptor = ArgumentCaptor.forClass(Users.class);
        //when
        userService.signup(signupRequest);

        // then
        verify(userRepository).save(userCaptor.capture());
        Users savedUser = userCaptor.getValue();

        assertFalse(savedUser.getIsDeleted(), "회원가입 시 isDeleted는 false여야 합니다.");

    }
    
    @Test
    @DisplayName("이메일이 중복이 아닐 때 false 리턴")
    void isEmailDuplicate_withNonExistingEmail_shouldReturnTrue() {
        // given
        String email = "test1@mail.com";
        when(userRepository.existsByEmail(email)).thenReturn(false);

        //when
        boolean result = userService.isEmailDuplicate(email);
        
        // then
        assertFalse(result, "중복이 아닌 이메일일 경우 false를 반환");
        
    }

    @Test
    @DisplayName("이메일이 중복일 때 true 리턴")
    void signup_withExistingEmail_shouldReturnTrue() {
        // given
        String email = "test@mail.com";

        when(userRepository.existsByEmail(email)).thenReturn(true);

        //when
        boolean result = userService.isEmailDuplicate(email);

        // then
        assertTrue(result, "중복인 이메일일 경우 true를 반환");

    }

    @Test
    @DisplayName("닉네임이 중복이 아닐 때 false 리턴")
    void signup_withNonExistingNickname_shouldReturnFalse() {
        // given
        String nickname = "nickname";

        when(userRepository.existsByNickname(nickname)).thenReturn(false);
        //when
        boolean result = userService.isNicknameDuplicate(nickname);

        // then
        assertFalse(result, "중복이 아닌 닉네임일 경우 false를 반환");

    }

    @Test
    @DisplayName("닉네임이 중복일 경우 true 리턴")
    void signup_withExistingNickname_shouldReturnTrue() {
        // given
        String nickname = "nickname";

        when(userRepository.existsByNickname(nickname)).thenReturn(true);

        //when
        boolean result = userService.isNicknameDuplicate(nickname);

        // then
        assertTrue(result, "중복인 닉네임일 경우 true를 반환");

    }

    @Test
    @DisplayName("유효한 이메일, 비밀번호로 로그인 시 토큰 발급 및 loginCheck true return")
    void login_withValidEmailAndPassword_shouldReturnAccessAndRefreshToken() {
        // given
        Users user = Users.builder()
                .email("test@mail.com")
                .password("encodePassword")
                .nickname("nickname")
                .birthDate(LocalDate.of(1995, 4, 3))
                .provider(Provider.EMAIL)
                .providerId(null)
                .pointBalance(0)
                .isDeleted(false)
                .build();

        LoginRequest loginRequest = new LoginRequest("test@mail.com", "!test1234");
        Authentication authentication = mock(Authentication.class);

        when(authenticationManager.authenticate(any())).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(mockPrincipalUser);
        when(mockPrincipalUser.getUsername()).thenReturn("test@mail.com");
        when(mockPrincipalUser.getId()).thenReturn(1L);
        when(mockPrincipalUser.getUsers()).thenReturn(user);

        when(jwtProvider.generateAccessToken(any(), any())).thenReturn("access-token");
        when(jwtProvider.generateRefreshToken(any())).thenReturn("refresh-token");

        when(redisTemplate.opsForValue()).thenReturn(valueOperations);

        //when
        JwtTokenResponse result = userService.login(loginRequest, response);

        // then
        assertTrue(result.getIsLogin(), "login check는 true여야 합니다.");
        assertEquals("access-token", result.getAccessToken(), "accessToken이 올바르지 않습니다.");
        assertEquals(Provider.EMAIL, result.getProvider(), "provider는 EMAIL이어야 합니다.");

    }

    @Test
    @DisplayName("유효한 이메일, 비밀번호로 로그인 시 토큰 중 하나라도 발급 실패")
    void login_withValidEmailAndPasswordButTokenCreationFails_shouldReturnLoginCheckFalse() {
        // given
        Users user = Users.builder()
                .email("test@mail.com")
                .password("encodePassword")
                .nickname("nickname")
                .birthDate(LocalDate.of(1995, 4, 3))
                .provider(Provider.EMAIL)
                .providerId(null)
                .pointBalance(0)
                .isDeleted(false)
                .build();

        LoginRequest loginRequest = new LoginRequest("test@mail.com", "!test1234");
        Authentication authentication = mock(Authentication.class);

        when(authenticationManager.authenticate(any())).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(mockPrincipalUser);

        when(jwtProvider.generateAccessToken(any(), any())).thenReturn(null);
        when(jwtProvider.generateRefreshToken(any())).thenReturn("refresh-token");


        //when

        CustomException exception = assertThrows(CustomException.class, () -> {
            userService.login(loginRequest, response);
        });

        // then
        assertEquals("Refresh Token 또는 Access Token 생성에 실패했습니다.", exception.getErrorMessage());
        assertEquals(ErrorCode.TOKEN_GENERATION_FAILED, ErrorCode.valueOf(exception.getErrorCode()));
    }

    @Test
    @DisplayName("이메일이 존재하지 않거나 비밀번호가 틀려 인증 실패")
    void login_withInvalidEmailOrPassword_shouldThrowAuthenticationException() {
        // given
        LoginRequest loginRequest = new LoginRequest("test@mail.com", "!test1234");

        when(authenticationManager.authenticate(any())).thenThrow(new BadCredentialsException("잘못된 이메일 또는 비밀번호입니다."));
        //when

        BadCredentialsException exception = assertThrows(BadCredentialsException.class, () -> {
            userService.login(loginRequest, response);
        });

        // then
        assertEquals("잘못된 이메일 또는 비밀번호입니다.", exception.getMessage());
        
    }

    @Test
    @DisplayName("토큰 발급에 성공했지만 redis에 저장 중 CustomException 발생")
    void login_whenRedisStorageFails_shouldThrowCustomException() {
        // given
        LoginRequest loginRequest = new LoginRequest("test@mail.com", "!test1234");

        Authentication authentication = mock(Authentication.class);
        when(authenticationManager.authenticate(any())).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(mockPrincipalUser);
        when(mockPrincipalUser.getUsername()).thenReturn("test@mail.com");
        when(mockPrincipalUser.getId()).thenReturn(1L);

        when(jwtProvider.generateAccessToken(any(), any())).thenReturn("access-token");
        when(jwtProvider.generateRefreshToken(any())).thenReturn("refresh-token");

        when(redisTemplate.opsForValue()).thenReturn(valueOperations);

        when(redisTemplate.opsForValue()).thenThrow(new CustomException(ErrorCode.REDIS_CONNECTION_FAILED));

        //when
        CustomException exception = assertThrows(CustomException.class, () -> {
            userService.login(loginRequest, response);
        });

        // then
        assertEquals("Redis 연결에 실패했습니다.", exception.getErrorMessage());
        assertEquals(ErrorCode.REDIS_CONNECTION_FAILED, ErrorCode.valueOf(exception.getErrorCode()));

    }
    
    @Test
    @DisplayName("정상적인 로그아웃 후 쿠키 제거 및 토큰 제거")
    void logout_withValidUser_shouldDeletedCookieAndDeleteToken() {
        // given
        Long userId = 1L;
        Users user = Users.builder()
                .id(userId)
                .email("test@mail.com")
                .password("encodePassword")
                .nickname("nickname")
                .birthDate(LocalDate.of(1995, 4, 3))
                .provider(Provider.EMAIL)
                .providerId(null)
                .pointBalance(0)
                .isDeleted(false)
                .build();

        when(mockPrincipalUser.getId()).thenReturn(userId);
        when(userRepository.findByIdAndIsDeletedFalse(userId)).thenReturn(Optional.of(user));
        request = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);

        when(request.getCookies()).thenReturn(new Cookie[]{
                new Cookie("refreshToken", "sample-refresh-token")
        });
        TokenInfo tokenInfo = new TokenInfo(1L, "test@mail.com");

        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get("sample-refresh-token")).thenReturn(tokenInfo);

        ArgumentCaptor<Cookie> cookieCaptor = ArgumentCaptor.forClass(Cookie.class);
        doNothing().when(response).addCookie(cookieCaptor.capture());

        //when
        userService.logout(mockPrincipalUser, request, response);
        
        // then
        verify(userRepository).findByIdAndIsDeletedFalse(1L);
        verify(valueOperations).get("sample-refresh-token");
        verify(redisTemplate).delete("sample-refresh-token");

        Cookie clearedCookie = cookieCaptor.getValue();
        assertEquals("refreshToken", clearedCookie.getName());
        assertNull(clearedCookie.getValue());
        assertEquals(0, clearedCookie.getMaxAge());
    }
    
    @Test
    @DisplayName("존재하지 않는 사용자일 경우 CustomException 발생")
    void logout_withNonExistUser_shouldThrowCustomException() {
        // given
        Long userId = 1L;

        when(mockPrincipalUser.getId()).thenReturn(userId);
        when(userRepository.findByIdAndIsDeletedFalse(userId)).thenThrow(new CustomException(ErrorCode.USER_NOT_FOUND));

        //when
        CustomException exception = assertThrows(CustomException.class, () -> {
           userService.logout(mockPrincipalUser, request, response);
        });
        
        // then
        assertEquals("존재하지 않는 사용자입니다.", exception.getErrorMessage());
        assertEquals(ErrorCode.USER_NOT_FOUND, ErrorCode.valueOf(exception.getErrorCode()));
        
    }
    
    @Test
    @DisplayName("refreshToken이 없으면 CustomException 발생")
    void logout_withoutRefreshToken_shouldThrowCustomException() {
        // given
        Long userId = 1L;

        Users user = Users.builder()
                .id(userId)
                .email("test@mail.com")
                .password("encodePassword")
                .nickname("nickname")
                .birthDate(LocalDate.of(1995, 4, 3))
                .provider(Provider.EMAIL)
                .providerId(null)
                .pointBalance(0)
                .isDeleted(false)
                .build();

        when(mockPrincipalUser.getId()).thenReturn(userId);
        when(userRepository.findByIdAndIsDeletedFalse(userId)).thenReturn(Optional.of(user));
        request = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);

        Cookie cookie = new Cookie("NoRefreshToken", "sample-refresh-token");
        when(request.getCookies()).thenReturn(new Cookie[]{
                cookie
        });

        //when
        CustomException exception = assertThrows(CustomException.class, () -> {
            userService.logout(mockPrincipalUser, request, response);
        });

        // then
        assertEquals("Refresh Token이 존재하지 않습니다.", exception.getErrorMessage());
        assertEquals(ErrorCode.REFRESH_TOKEN_NOT_FOUND, ErrorCode.valueOf(exception.getErrorCode()));
        
    }

    @Test
    @DisplayName("이메일 회원일 경우 임시 비밀번호 발급 후 메일 전송")
    void passwordFind_withExistingEmailUser_shouldSendTempPasswordByEmail() {
        // given
        String email = "test@mail.com";
        FindPasswordRequest findPasswordRequest = new FindPasswordRequest(email);

        Users user = Users.builder()
                .id(1L)
                .email(email)
                .password("encodePassword")
                .nickname("nickname")
                .birthDate(LocalDate.of(1995, 4, 3))
                .provider(Provider.EMAIL)
                .providerId(null)
                .pointBalance(0)
                .isDeleted(false)
                .build();

        when(userRepository.findByEmailAndIsDeletedFalse(email)).thenReturn(Optional.of(user));

        //when
        Map<String, String> result = userService.passwordFind(findPasswordRequest);

        // then
        verify(mailService).sendTemporaryPassword(eq(email), any());
        assertEquals("email", result.get("type"));
        assertEquals("임시 비밀번호가 메일로 발송되었습니다.", result.get("message"));

    }

    @Test
    @DisplayName("소셜 로그인 회원일 경우 비밀번호 발급 없이 안내메세지 반환")
    void passwordFind_withSocialProviderUser_shouldReturnSocialMessage() {
        // given
        String email = "test@mail.com";
        Provider provider = Provider.NAVER;
        FindPasswordRequest findPasswordRequest = new FindPasswordRequest(email);

        Users user = Users.builder()
                .id(1L)
                .email(email)
                .password("encodePassword")
                .nickname("nickname")
                .birthDate(LocalDate.of(1995, 4, 3))
                .provider(provider)
                .providerId(null)
                .pointBalance(0)
                .isDeleted(false)
                .build();

        when(userRepository.findByEmailAndIsDeletedFalse(email)).thenReturn(Optional.of(user));
        //when
        Map<String, String> result = userService.passwordFind(findPasswordRequest);

        // then
        assertEquals("social", result.get("type"));
        assertEquals(String.format("%s로 회원가입된 이메일입니다.", user.getProvider()), result.get("message"));

    }

    @Test
    @DisplayName("존재하지 않는 이메일일 경우 안내메세지 리턴")
    void passwordFind_withNonExistingEmailUser_shouldReturnNoUserMessage() {
        // given
        String email = "test@mail.com";

        FindPasswordRequest findPasswordRequest = new FindPasswordRequest(email);
        when(userRepository.findByEmailAndIsDeletedFalse(email)).thenReturn(Optional.empty());

        //when
        Map<String, String> result = userService.passwordFind(findPasswordRequest);

        // then
        assertEquals("noUser", result.get("type"));
        assertEquals("회원가입되지 않은 이메일입니다.", result.get("message"));

    }
    
    @Test
    @DisplayName("존재하는 사용자의 정보를 조회하면 UserInfoResponse를 리턴")
    void getMyInfo_withValidPrincipal_shouldReturnUserInfoResponse() {
        // given
        String email = "test@mail.com";
        Users user = Users.builder()
                .id(1L)
                .email(email)
                .password("encodePassword")
                .nickname("nickname")
                .birthDate(LocalDate.of(1995, 4, 3))
                .provider(Provider.EMAIL)
                .providerId(null)
                .pointBalance(0)
                .isDeleted(false)
                .build();

        when(mockPrincipalUser.getUsername()).thenReturn(email);
        when(userRepository.findByEmailAndIsDeletedFalse(email)).thenReturn(Optional.of(user));

        //when
        UserInfoResponse userInfoResponse = userService.getMyInfo(mockPrincipalUser);
        
        // then
        assertEquals(email, userInfoResponse.getEmail());
        assertEquals("nickname", userInfoResponse.getNickname());
        assertEquals(LocalDate.of(1995, 4, 3), userInfoResponse.getBirthDate());
        
    }
    
    @Test
    @DisplayName("존재하지 않는 사용자의 정보를 조회하면 CustomException 발생")
    void getMyInfo_withInvalidPrincipal_shouldThrowCustomException() {
        // given
        String email = "test@mail.com";

        when(mockPrincipalUser.getUsername()).thenReturn(email);
        when(userRepository.findByEmailAndIsDeletedFalse(email)).thenThrow(new CustomException(ErrorCode.USER_NOT_FOUND));
        //when
        CustomException exception = assertThrows(CustomException.class, () -> {
            userService.getMyInfo(mockPrincipalUser);
        });
        
        // then
        assertEquals("존재하지 않는 사용자입니다.", exception.getErrorMessage());
        assertEquals(ErrorCode.USER_NOT_FOUND, ErrorCode.valueOf(exception.getErrorCode()));
        
    }
    
    @Test
    @DisplayName("입력한 비밀번호와 사용자의 정보 속 비밀번호가 일치하면 true 리턴")
    void checkPassword_withCorrectPassword_shouldReturnTrue() {
        // given
        String password = "encodePassword";
        Users user = Users.builder()
                .id(1L)
                .email("test@mail.com")
                .password(password)
                .nickname("nickname")
                .birthDate(LocalDate.of(1995, 4, 3))
                .provider(Provider.EMAIL)
                .providerId(null)
                .pointBalance(0)
                .isDeleted(false)
                .build();

        when(userRepository.findByIdAndIsDeletedFalse(1L)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(password, "encodePassword")).thenReturn(true);
        //when
        boolean result = userService.checkPassword(1L, password);
        
        // then
        assertTrue(result);
        
    }
    
    @Test
    @DisplayName("존재하지 않는 사용자를 조회하면 CustomException 발생")
    void checkPassword_withInvalidUser_shouldThrowCustomException() {
        // given
        String password = "encodePassword";

        when(userRepository.findByIdAndIsDeletedFalse(1L)).thenThrow(new CustomException(ErrorCode.USER_NOT_FOUND));
        //when
        CustomException exception = assertThrows(CustomException.class, () -> {
            userService.checkPassword(1L, password);
        });
        
        // then
        assertEquals("존재하지 않는 사용자입니다.", exception.getErrorMessage());
        assertEquals(ErrorCode.USER_NOT_FOUND, ErrorCode.valueOf(exception.getErrorCode()));
        
    }
    
    @Test
    @DisplayName("입력한 비밀번호와 사용자 정보 속 비밀번호가 일치하지 않으면 false 리턴")
    void checkPassword_withWrongPassword_shouldReturnFalse() {
        // given
        String password = "encodePassword";
        Users user = Users.builder()
                .id(1L)
                .email("test@mail.com")
                .password(password)
                .nickname("nickname")
                .birthDate(LocalDate.of(1995, 4, 3))
                .provider(Provider.EMAIL)
                .providerId(null)
                .pointBalance(0)
                .isDeleted(false)
                .build();

        when(userRepository.findByIdAndIsDeletedFalse(1L)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(anyString(), eq(password))).thenReturn(false);
        //when
        boolean result = userService.checkPassword(1L, password);
        
        // then
        assertFalse(result);
        
    }
    
    @Test
    @DisplayName("닉네임만 수정할 경우 닉네임이 정상 변경됨")
    void updateProfile_withValidNicknameOnly_shouldUpdateNickname() {
        // given
        Long userId = 1L;
        Users user = Users.builder()
                .id(userId)
                .email("test@mail.com")
                .password("password")
                .nickname("nickname")
                .birthDate(LocalDate.of(1995, 4, 3))
                .provider(Provider.EMAIL)
                .providerId(null)
                .pointBalance(0)
                .isDeleted(false)
                .build();

        String newNickname = "newNick";
        when(userRepository.findByIdAndIsDeletedFalse(userId)).thenReturn(Optional.of(user));
        UserUpdateRequest userUpdateRequest = new UserUpdateRequest(null, newNickname);

        //when
        userService.updateProfile(userId, userUpdateRequest);

        // then
        assertEquals(newNickname, user.getNickname());

    }

    @Test
    @DisplayName("비밀번호만 수정할 경우 비밀번호가 정상 변경됨")
    void updateProfile_withValidPasswordOnly_shouldUpdatePassword() {
        // given
        String newPassword = "newPassword";
        Long userId = 1L;
        Users user = Users.builder()
                .id(1L)
                .email("test@mail.com")
                .password("password")
                .nickname("nickname")
                .birthDate(LocalDate.of(1995, 4, 3))
                .provider(Provider.EMAIL)
                .providerId(null)
                .pointBalance(0)
                .isDeleted(false)
                .build();

        when(userRepository.findByIdAndIsDeletedFalse(userId)).thenReturn(Optional.of(user));
        when(passwordEncoder.encode(anyString())).thenReturn(newPassword);

        UserUpdateRequest userUpdateRequest = new UserUpdateRequest("!test1234", null);
        //when
        userService.updateProfile(userId, userUpdateRequest);

        // then
        assertEquals(newPassword, user.getPassword());

    }

    @Test
    @DisplayName("닉네임과 비밀번호 모두 수정할 경우 둘 다 반영됨")
    void updateProfile_withValidNicknameAndPassword_shouldUpdateBoth() {
        // given
        Long userId = 1L;
        String newPassword = "newPassword";
        String newNickname = "newNickname";
        Users user = Users.builder()
                .id(1L)
                .email("test@mail.com")
                .password("password")
                .nickname("nickname")
                .birthDate(LocalDate.of(1995, 4, 3))
                .provider(Provider.EMAIL)
                .providerId(null)
                .pointBalance(0)
                .isDeleted(false)
                .build();

        when(userRepository.findByIdAndIsDeletedFalse(userId)).thenReturn(Optional.of(user));
        when(passwordEncoder.encode(anyString())).thenReturn(newPassword);

        UserUpdateRequest userUpdateRequest = new UserUpdateRequest("!test1234", newNickname);

        //when
        userService.updateProfile(userId, userUpdateRequest);

        // then
        assertEquals(newNickname, user.getNickname());
        assertEquals(newPassword, user.getPassword());
    }

    @Test
    @DisplayName("존재하지 않는 사용자 ID로 수정 시 CustomException 발생")
    void updateProfile_withInvalidUserId_shouldThrowCustomException() {
        // given
        Long userId = 1L;

        when(userRepository.findByIdAndIsDeletedFalse(userId)).thenThrow(new CustomException(ErrorCode.USER_NOT_FOUND));

        UserUpdateRequest userUpdateRequest = new UserUpdateRequest();
        //when
        CustomException exception = assertThrows(CustomException.class, () -> {
           userService.updateProfile(userId, userUpdateRequest);
        });

        // then
        assertEquals("존재하지 않는 사용자입니다.", exception.getErrorMessage());
        assertEquals(ErrorCode.USER_NOT_FOUND, ErrorCode.valueOf(exception.getErrorCode()));

    }

    @Test
    @DisplayName("비밀번호 형식이 유효하지 않을 경우 예외 발생")
    void updateProfile_withInvalidPassword_shouldThrowCustomException() {
        // given
        Long userId = 1L;
        Users user = Users.builder()
                .id(userId)
                .email("test@mail.com")
                .password("password")
                .nickname("nickname")
                .birthDate(LocalDate.of(1995, 4, 3))
                .provider(Provider.EMAIL)
                .providerId(null)
                .pointBalance(0)
                .isDeleted(false)
                .build();

        when(userRepository.findByIdAndIsDeletedFalse(userId)).thenReturn(Optional.of(user));

        String invalidPassword = "abc1234";
        UserUpdateRequest userUpdateRequest = new UserUpdateRequest(invalidPassword, null);

        //when
        CustomException exception = assertThrows(CustomException.class, () -> {
            userService.updateProfile(userId, userUpdateRequest);
        });

        // then
        assertEquals("비밀번호는 8자 이상, 영문, 숫자, 특수문자(!@-#$%&^)를 모두 포함해야 합니다.", exception.getErrorMessage());
        assertEquals(ErrorCode.PASSWORD_POLICY_VIOLATION, ErrorCode.valueOf(exception.getErrorCode()));
    }

    @Test
    @DisplayName("이메일 회원 탈퇴 시 비밀번호가 일치하면 계정 삭제")
    void deleteAccount_withCorrectPassword_shouldDeleteAccount() {
        // given
        Long userId = 1L;
        String password = "password";
        Users user = Users.builder()
                .id(userId)
                .email("test@mail.com")
                .password(password)
                .nickname("nickname")
                .birthDate(LocalDate.of(1995, 4, 3))
                .provider(Provider.EMAIL)
                .providerId(null)
                .pointBalance(0)
                .isDeleted(false)
                .build();

        when(userRepository.findByIdAndIsDeletedFalse(userId)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(anyString(), eq(password))).thenReturn(true);
        request = mock(HttpServletRequest.class);

        Cookie cookie = new Cookie("refreshToken", "sample-refresh-token");
        when(request.getCookies()).thenReturn(new Cookie[]{
                cookie
        });

        TokenInfo tokenInfo = new TokenInfo(1L, "test@mail.com");

        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get("sample-refresh-token")).thenReturn(tokenInfo);

        //when
        Map<String, Object> result = userService.deleteAccount(userId, password, request);

        // then
        assertTrue((boolean) result.get("isDeleted"));
        assertEquals("계정이 삭제되었습니다.", result.get("message"));

    }

    @Test
    @DisplayName("이메일 회원 탈퇴 시 비밀번호가 일치하지 않으면 계정 삭제 안됨")
    void deleteAccount_withInvalidPassword_shouldNotDeleteAccount() {
        // given
        Long userId = 1L;
        String password = "password";
        Users user = Users.builder()
                .id(userId)
                .email("test@mail.com")
                .password(password)
                .nickname("nickname")
                .birthDate(LocalDate.of(1995, 4, 3))
                .provider(Provider.EMAIL)
                .providerId(null)
                .pointBalance(0)
                .isDeleted(false)
                .build();

        request = mock(HttpServletRequest.class);
        when(userRepository.findByIdAndIsDeletedFalse(userId)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(anyString(), eq(password))).thenReturn(false);

        //when
        Map<String, Object> result = userService.deleteAccount(userId, password, request);

        // then
        assertFalse((boolean) result.get("isDeleted"));
        assertEquals("비밀번호가 일치하지 않습니다.", result.get("message"));

    }

    @Test
    @DisplayName("존재하지 않는 사용장일 경우 CustomException 발생")
    void deleteAccount_withInvalidUser_shouldThrowCustomException() {
        // given
        Long userId = 1L;
        String password = "password";
        request = mock(HttpServletRequest.class);

        when(userRepository.findByIdAndIsDeletedFalse(1L)).thenThrow(new UsernameNotFoundException("존재하지 않는 사용자입니다."));

        //when
        UsernameNotFoundException exception = assertThrows(UsernameNotFoundException.class, () -> {
            userService.deleteAccount(userId, password, request);
        });


        // then
        assertEquals("존재하지 않는 사용자입니다.", exception.getMessage());

    }

    @Test
    @DisplayName("카카오 소셜 로그인 유저는 연결 해제 후 계정 삭제된다.")
    void deleteAccount_withKakaoUser_shouldUnlinkAndDelete() {
        // given
        Long userId = 1L;
        String password = "password";
        Users user = Users.builder()
                .id(userId)
                .email("test@kakao.com")
                .password(password)
                .nickname("nickname")
                .birthDate(LocalDate.of(1995, 4, 3))
                .provider(Provider.KAKAO)
                .providerId("sampleProviderId")
                .pointBalance(0)
                .isDeleted(false)
                .build();

        request = mock(HttpServletRequest.class);
        when(userRepository.findByIdAndIsDeletedFalse(userId)).thenReturn(Optional.of(user));

        String socialAccessToken = "social-access-token";
        String redisKey = String.format("%d_%s_accessToken", user.getId(), user.getProviderId());

        AccessToken accessToken = new AccessToken(userId, socialAccessToken);
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get(redisKey)).thenReturn(accessToken);

        when(request.getCookies()).thenReturn(new Cookie[]{
                new Cookie("refreshToken", "sample-refresh-token")
        });

        TokenInfo tokenInfo = new TokenInfo(1L, "test@mail.com");
        when(valueOperations.get("sample-refresh-token")).thenReturn(tokenInfo);

        when(restTemplate.postForEntity(anyString(), any(HttpEntity.class), eq(String.class)))
                .thenReturn(new ResponseEntity<>("ok", HttpStatus.OK));

        //when
        Map<String, Object> result = userService.deleteAccount(userId, null, request);

        // then
        assertTrue((boolean) result.get("isDeleted"));
        assertEquals("카카오 계정이 삭제되었습니다.", result.get("message"));

        verify(redisTemplate).delete(redisKey);
    }

    @Test
    @DisplayName("네이버 소셜 로그인 유저는 연결 해제 후 계정 삭제된다.")
    void deleteAccount_withNaverUser_shouldUnlinkAndDelete() {
        // given
        Long userId = 1L;
        String password = "password";
        Users user = Users.builder()
                .id(userId)
                .email("test@naver.com")
                .password(password)
                .nickname("nickname")
                .birthDate(LocalDate.of(1995, 4, 3))
                .provider(Provider.NAVER)
                .providerId("sampleProviderId")
                .pointBalance(0)
                .isDeleted(false)
                .build();

        request = mock(HttpServletRequest.class);
        when(userRepository.findByIdAndIsDeletedFalse(userId)).thenReturn(Optional.of(user));

        String socialAccessToken = "social-access-token";
        String redisKey = String.format("%d_%s_accessToken", user.getId(), user.getProviderId());

        AccessToken accessToken = new AccessToken(userId, socialAccessToken);
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get(redisKey)).thenReturn(accessToken);

        when(request.getCookies()).thenReturn(new Cookie[]{
                new Cookie("refreshToken", "sample-refresh-token")
        });

        TokenInfo tokenInfo = new TokenInfo(1L, "test@mail.com");
        when(valueOperations.get("sample-refresh-token")).thenReturn(tokenInfo);

        when(restTemplate.postForEntity(anyString(), any(HttpEntity.class), eq(String.class)))
                .thenReturn(new ResponseEntity<>("ok", HttpStatus.OK));

        //when
        Map<String, Object> result = userService.deleteAccount(userId, null, request);

        // then
        assertTrue((boolean) result.get("isDeleted"));
        assertEquals("네이버 계정이 삭제되었습니다.", result.get("message"));

        verify(redisTemplate).delete(redisKey);
    }

    @Test
    @DisplayName("구글 소셜 로그인 유저는 연결 해제 후 계정 삭제된다.")
    void deleteAccount_withGoogleUser_shouldUnlinkAndDelete() {
        // given
        Long userId = 1L;
        String password = "password";
        Users user = Users.builder()
                .id(userId)
                .email("test@gmail.com")
                .password(password)
                .nickname("nickname")
                .birthDate(LocalDate.of(1995, 4, 3))
                .provider(Provider.GOOGLE)
                .providerId("sampleProviderId")
                .pointBalance(0)
                .isDeleted(false)
                .build();

        request = mock(HttpServletRequest.class);
        when(userRepository.findByIdAndIsDeletedFalse(userId)).thenReturn(Optional.of(user));

        String socialAccessToken = "social-access-token";
        String redisKey = String.format("%d_%s_accessToken", user.getId(), user.getProviderId());

        AccessToken accessToken = new AccessToken(userId, socialAccessToken);
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get(redisKey)).thenReturn(accessToken);

        when(request.getCookies()).thenReturn(new Cookie[]{
                new Cookie("refreshToken", "sample-refresh-token")
        });

        TokenInfo tokenInfo = new TokenInfo(1L, "test@mail.com");
        when(valueOperations.get("sample-refresh-token")).thenReturn(tokenInfo);

        when(restTemplate.postForEntity(anyString(), any(HttpEntity.class), eq(String.class)))
                .thenReturn(new ResponseEntity<>("ok", HttpStatus.OK));

        //when
        Map<String, Object> result = userService.deleteAccount(userId, null, request);

        // then
        assertTrue((boolean) result.get("isDeleted"));
        assertEquals("구글 계정이 삭제되었습니다.", result.get("message"));

        verify(redisTemplate).delete(redisKey);
    }
}
