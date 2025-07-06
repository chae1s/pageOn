package com.pageon.backend.service;

import com.pageon.backend.dto.AccessToken;
import com.pageon.backend.dto.oauth.GoogleSignupRequest;
import com.pageon.backend.dto.oauth.KakaoSignupRequest;
import com.pageon.backend.dto.oauth.NaverSignupRequest;
import com.pageon.backend.dto.oauth.OAuthUserInfoResponse;
import com.pageon.backend.entity.Users;
import com.pageon.backend.entity.enums.Provider;
import com.pageon.backend.repository.UserRepository;
import com.pageon.backend.security.CustomOauth2UserService;
import com.pageon.backend.security.PrincipalUser;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.test.context.ActiveProfiles;
import static org.mockito.Mockito.*;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;


@Transactional
@ActiveProfiles("test")
@DisplayName("customOauth2UserService 단위 테스트")
@ExtendWith(MockitoExtension.class)
class CustomOauth2UserServiceTest {

    @Mock
    private DefaultOAuth2UserService delegate;
    @InjectMocks
    private CustomOauth2UserService customOauth2UserService;
    @Mock
    private UserRepository userRepository;
    @Mock
    private RoleService roleService;
    @Mock
    private RedisTemplate<String, Object> redisTemplate;
    @Mock
    private ValueOperations<String, Object> valueOperations;
    @Mock
    private ClientRegistration clientRegistration;


    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
    }
    @Test
    @DisplayName("카카오 로그인 회원이 존재하지 않으면 신규 가입")
    void signupKakao_shouldCreateNewUser() {
        // given
        Map<String, Object> kakaoAccount = new HashMap<>();
        kakaoAccount.put("email", "test@kakao.com");

        Map<String, Object> attribute = new HashMap<>();
        attribute.put("id", "providerId");
        attribute.put("kakao_account", kakaoAccount);

        OAuthUserInfoResponse oAuthUserInfoResponse = new KakaoSignupRequest(attribute);

        //when
        Users newUser = customOauth2UserService.signupSocial(oAuthUserInfoResponse);

        // then
        assertEquals("test@kakao.com", newUser.getEmail());
        assertFalse(newUser.getIsDeleted());
        assertEquals(Provider.KAKAO, newUser.getProvider());
        verify(roleService).assignDefaultRole(newUser);
        verify(userRepository).save(newUser);
    }

    @Test
    @DisplayName("네이버 로그인 회원이 존재하지 않으면 신규 가입")
    void signupNaver_shouldCreateNewUser() {
        // given
        OAuthUserInfoResponse oAuthUserInfoResponse = new NaverSignupRequest(Map.of(
                "id", "providerId",
                "email", "test@naver.com"
        ));

        //when
        Users newUser = customOauth2UserService.signupSocial(oAuthUserInfoResponse);

        // then
        assertEquals("test@naver.com", newUser.getEmail());
        assertFalse(newUser.getIsDeleted());
        assertEquals(Provider.NAVER, newUser.getProvider());
        verify(roleService).assignDefaultRole(newUser);
        verify(userRepository).save(newUser);
    }

    @Test
    @DisplayName("구글 로그인 회원이 존재하지 않으면 신규 가입")
    void signupGoogle_shouldCreateNewUser() {
        // given
        OAuthUserInfoResponse oAuthUserInfoResponse = new GoogleSignupRequest(Map.of(
                "sub", "providerId",
                "email", "test@gmail.com"
        ));

        //when
        Users newUser = customOauth2UserService.signupSocial(oAuthUserInfoResponse);

        // then
        assertEquals("test@gmail.com", newUser.getEmail());
        assertFalse(newUser.getIsDeleted());
        assertEquals(Provider.GOOGLE, newUser.getProvider());
        verify(roleService).assignDefaultRole(newUser);
        verify(userRepository).save(newUser);
    }
    
    @Test
    @DisplayName("카카오 소셜 로그인 성공 시 accessToken 저장 및 기존 사용자 반환")
    void loadUser_withKakao_shouldStoreAccessTokenAndReturnUser() {
        // given
        OAuth2User oAuth2User = mock(OAuth2User.class);
        when(oAuth2User.getAttributes()).thenReturn(Map.of(
                "id", "123456",
                "kakao_account", Map.of(
                        "email", "test@kakao.com"
                )
        ));
        OAuth2UserRequest request = mock(OAuth2UserRequest.class);
        when(request.getClientRegistration()).thenReturn(clientRegistration);
        when(clientRegistration.getRegistrationId()).thenReturn("kakao");

        OAuth2AccessToken oAuth2AccessToken = new OAuth2AccessToken(
                OAuth2AccessToken.TokenType.BEARER,
                "social-access-token",
                Instant.now(),
                Instant.now().plusSeconds(3600)
        );
        when(request.getAccessToken()).thenReturn(oAuth2AccessToken);

        when(delegate.loadUser(request)).thenReturn(oAuth2User);

        Users user = Users.builder()
                .id(1L)
                .email("test@kakao.com")
                .nickname("카카오")
                .provider(Provider.KAKAO)
                .providerId("123456")
                .isDeleted(false)
                .build();

        when(redisTemplate.opsForValue()).thenReturn(valueOperations);

        when(userRepository.findWithRolesByProviderAndProviderId(Provider.KAKAO, "123456")).thenReturn(Optional.of(user));

        
        //when
        OAuth2User result = customOauth2UserService.loadUser(request);
        
        // then
        verify(valueOperations).set(eq(String.format("%d_123456_accessToken", 1L)), any(AccessToken.class));
        assertTrue(result instanceof PrincipalUser);
        PrincipalUser principal = (PrincipalUser) result;
        assertEquals("test@kakao.com", principal.getUsername());
        
    }

    @Test
    @DisplayName("네이버 소셜 로그인 성공 시 accessToken 저장 및 기존 사용자 반환")
    void loadUser_withNaver_shouldStoreAccessTokenAndReturnUser() {
        // given
        OAuth2User oAuth2User = mock(OAuth2User.class);
        when(oAuth2User.getAttributes()).thenReturn(Map.of(
                "response", Map.of(
                        "id", "123456",
                        "email", "test@naver.com"
                )
        ));
        OAuth2UserRequest request = mock(OAuth2UserRequest.class);
        when(request.getClientRegistration()).thenReturn(clientRegistration);
        when(clientRegistration.getRegistrationId()).thenReturn("naver");

        OAuth2AccessToken oAuth2AccessToken = new OAuth2AccessToken(
                OAuth2AccessToken.TokenType.BEARER,
                "social-access-token",
                Instant.now(),
                Instant.now().plusSeconds(3600)
        );
        when(request.getAccessToken()).thenReturn(oAuth2AccessToken);

        when(delegate.loadUser(request)).thenReturn(oAuth2User);

        Users user = Users.builder()
                .id(1L)
                .email("test@naver.com")
                .nickname("네이버")
                .provider(Provider.NAVER)
                .providerId("123456")
                .isDeleted(false)
                .build();

        when(redisTemplate.opsForValue()).thenReturn(valueOperations);

        when(userRepository.findWithRolesByProviderAndProviderId(Provider.NAVER, "123456")).thenReturn(Optional.of(user));


        //when
        OAuth2User result = customOauth2UserService.loadUser(request);

        // then
        verify(valueOperations).set(eq(String.format("%d_123456_accessToken", 1L)), any(AccessToken.class));
        assertTrue(result instanceof PrincipalUser);
        PrincipalUser principal = (PrincipalUser) result;
        assertEquals("test@naver.com", principal.getUsername());

    }

    @Test
    @DisplayName("구글 소셜 로그인 성공 시 accessToken 저장 및 기존 사용자 반환")
    void loadUser_withGoogle_shouldStoreAccessTokenAndReturnUser() {
        // given
        OAuth2User oAuth2User = mock(OAuth2User.class);
        when(oAuth2User.getAttributes()).thenReturn(Map.of(
                "sub", "123456",
                "email", "test@google.com"
        ));
        OAuth2UserRequest request = mock(OAuth2UserRequest.class);
        when(request.getClientRegistration()).thenReturn(clientRegistration);
        when(clientRegistration.getRegistrationId()).thenReturn("google");

        OAuth2AccessToken oAuth2AccessToken = new OAuth2AccessToken(
                OAuth2AccessToken.TokenType.BEARER,
                "social-access-token",
                Instant.now(),
                Instant.now().plusSeconds(3600)
        );
        when(request.getAccessToken()).thenReturn(oAuth2AccessToken);

        when(delegate.loadUser(request)).thenReturn(oAuth2User);

        Users user = Users.builder()
                .id(1L)
                .email("test@google.com")
                .nickname("구글")
                .provider(Provider.GOOGLE)
                .providerId("123456")
                .isDeleted(false)
                .build();

        when(redisTemplate.opsForValue()).thenReturn(valueOperations);

        when(userRepository.findWithRolesByProviderAndProviderId(Provider.GOOGLE, "123456")).thenReturn(Optional.of(user));


        //when
        OAuth2User result = customOauth2UserService.loadUser(request);

        // then
        verify(valueOperations).set(eq(String.format("%d_123456_accessToken", 1L)), any(AccessToken.class));
        assertTrue(result instanceof PrincipalUser);
        PrincipalUser principal = (PrincipalUser) result;
        assertEquals("test@google.com", principal.getUsername());

    }


}