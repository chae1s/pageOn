package com.pageon.backend.service;

import com.pageon.backend.dto.JwtTokenResponse;
import com.pageon.backend.dto.TokenInfo;
import com.pageon.backend.entity.Users;
import com.pageon.backend.entity.enums.Provider;
import com.pageon.backend.repository.UserRepository;
import com.pageon.backend.security.JwtProvider;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@Transactional
@ActiveProfiles("test")
@DisplayName("AuthService 단위 테스트")
@ExtendWith(MockitoExtension.class)
class AuthServiceTest {
    @InjectMocks
    private AuthService authService;
    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;
    @Mock
    private RedisTemplate<String, Object> redisTemplate;
    @Mock
    private ValueOperations<String, Object> valueOperations;
    @Mock
    private JwtProvider jwtProvider;
    @Mock
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
    }
    
    @Test
    @DisplayName("accessToken 만료 후 유효한 refreshToken으로 새로운 accessToken 발급")
    void reissueToken_withValidRefreshToken_shouldReturnJwtTokenResponse() {
        // given
        String refreshToken = "sample-refresh-token";
        when(request.getCookies()).thenReturn(new Cookie[]{
                new Cookie("refreshToken", refreshToken)
        });

        TokenInfo tokenInfo = new TokenInfo(1L, "test@mail.com");
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get(refreshToken)).thenReturn(tokenInfo);

        when(jwtProvider.getUsernameRefreshToken(refreshToken)).thenReturn("test@mail.com");
        Users user = Users.builder()
                .id(1L)
                .email("test@mail.com")
                .isDeleted(false)
                .provider(Provider.EMAIL)
                .build();
        when(userRepository.findByEmailAndIsDeletedFalse("test@mail.com")).thenReturn(Optional.of(user));

        when(jwtProvider.generateAccessToken(eq("test@mail.com"), any())).thenReturn("reissue-access-token");

        //when
        JwtTokenResponse result = authService.reissueToken(request, response);
        
        // then
        assertEquals("reissue-access-token", result.getAccessToken());
        assertEquals(Provider.EMAIL, result.getProvider());
        assertTrue(result.getIsLogin());
        
    }
    
    @Test
    @DisplayName("cookie 안에 refreshToken이 존재하지 않을 때 RuntimeException 발생")
    void reissueToken_withNoExistingRefreshToken_shouldThrowRuntimeException() {
        // given
        when(request.getCookies()).thenReturn(new Cookie[]{});

        //when
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            authService.reissueToken(request, response);
        });
        
        // then
        assertEquals("쿠키에 Refresh Token이 없습니다.", exception.getMessage());
        
    }
    
    @Test
    @DisplayName("redis에 refreshToken로 저장된 정보가 존재하지 않을 때 RuntimeException 발생")
    void reissueToken_withNoExistingTokenInfo_shouldThrowRuntimeException() {
        // given
        String refreshToken = "sample-refresh-token";
        when(request.getCookies()).thenReturn(new Cookie[]{
                new Cookie("refreshToken", refreshToken)
        });

        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get(refreshToken)).thenReturn(null);

        //when
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            authService.reissueToken(request, response);
        });
        
        // then
        assertEquals("유효하지 않은 Refresh Token입니다.", exception.getMessage());
        
    }
    
    @Test
    @DisplayName("cookie에서 가져온 refresh token의 email정보로 가입된 User를 찾을 수 없을 때 UsernameNotFoundException 발생")
    void reissueToken_withInvalidEmail_shouldThrowUsernameNotFoundException() {
        // given
        String refreshToken = "sample-refresh-token";
        when(request.getCookies()).thenReturn(new Cookie[]{
                new Cookie("refreshToken", refreshToken)
        });

        String redisEmail = "redis_test@mail.com";
        TokenInfo tokenInfo = new TokenInfo(1L, redisEmail);
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get(refreshToken)).thenReturn(tokenInfo);

        String cookieEmail = "cookie_test@mail.com";
        when(jwtProvider.getUsernameRefreshToken(refreshToken)).thenReturn(cookieEmail);
        when(userRepository.findByEmailAndIsDeletedFalse(cookieEmail)).thenThrow(new UsernameNotFoundException("존재하지 않는 사용자입니다."));

        //when
        UsernameNotFoundException exception = assertThrows(UsernameNotFoundException.class, () -> {
            authService.reissueToken(request, response);
        });
        
        // then
        assertEquals("존재하지 않는 사용자입니다.", exception.getMessage());
        
    }
    
    @Test
    @DisplayName("cookie에서 가져온 refresh token의 user 정보와 redis에서 가져온 user 정보가 일치하지 않으면 RuntimeException 발생")
    void reissueToken_withNotMatchUserInformation_shouldThrowRuntimeException() {
        // given
        String refreshToken = "sample-refresh-token";
        when(request.getCookies()).thenReturn(new Cookie[]{
                new Cookie("refreshToken", refreshToken)
        });
        String redisEmail = "redis_test@mail.com";
        TokenInfo tokenInfo = new TokenInfo(1L, redisEmail);
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get(refreshToken)).thenReturn(tokenInfo);


        String cookieEmail = "cookie_test@mail.com";
        when(jwtProvider.getUsernameRefreshToken(refreshToken)).thenReturn(cookieEmail);
        Users user = Users.builder()
                .id(2L)
                .email(cookieEmail)
                .isDeleted(false)
                .provider(Provider.EMAIL)
                .build();
        when(userRepository.findByEmailAndIsDeletedFalse(cookieEmail)).thenReturn(Optional.of(user));


        //when
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            authService.reissueToken(request, response);
        });

        // then
        assertEquals("refresh token 소유자가 일치하지 않습니다.", exception.getMessage());
        
        
    }

}