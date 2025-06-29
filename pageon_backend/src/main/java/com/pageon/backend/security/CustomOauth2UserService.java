package com.pageon.backend.security;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pageon.backend.dto.AccessToken;
import com.pageon.backend.dto.TokenInfo;
import com.pageon.backend.dto.oauth.GoogleSignupRequest;
import com.pageon.backend.dto.oauth.KakaoSignupRequest;
import com.pageon.backend.dto.oauth.NaverSignupRequest;
import com.pageon.backend.dto.oauth.OAuthUserInfoResponse;
import com.pageon.backend.entity.Users;
import com.pageon.backend.repository.UserRepository;
import com.pageon.backend.service.RoleService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;
import java.util.Random;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomOauth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;
    private final RoleService roleService;
    private final RedisTemplate<String, Object> redisTemplate;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

        OAuth2User oAuth2User = super.loadUser(userRequest);


        log.info("OAuth2User Attributes: {}", userRequest.getClientRegistration());

        String registrationId = userRequest.getClientRegistration().getRegistrationId();

        OAuthUserInfoResponse oAuth2Response = null;
        Users users = null;
        if (registrationId.equals("kakao")) {
            log.info("kakao 로그인");
            oAuth2Response = new KakaoSignupRequest(oAuth2User.getAttributes());
            users = existingUser(oAuth2Response);
        } else if (registrationId.equals("naver")) {
            log.info("naver 로그인");
            Object response = oAuth2User.getAttributes().get("response");
            ObjectMapper mapper = new ObjectMapper();
            Map<String, Object> attribute = mapper.convertValue(response, new TypeReference<>() {});

            oAuth2Response = new NaverSignupRequest(attribute);
            users = existingUser(oAuth2Response);
        } else if (registrationId.equals("google")) {
            log.info("google 로그인");
            oAuth2Response = new GoogleSignupRequest(oAuth2User.getAttributes());

            users = existingUser(oAuth2Response);
        } else {
            throw new RuntimeException("소셜 로그인 실패");
        }

        setAccessToken(userRequest.getAccessToken(), users);
        return new PrincipalUser(users, oAuth2Response);
    }

    private Users existingUser(OAuthUserInfoResponse response) {
        log.info(response.getEmail());

        Optional<Users> user = userRepository.findWithRolesByProviderAndProviderId(response.getProvider(), response.getProviderId());

        if (user.isPresent()) {
            return user.get();
        }

        return signupSocial(response);
    }

    @Transactional
    public Users signupSocial(OAuthUserInfoResponse response) {

        Users users = Users.builder()
                .email(response.getEmail())
                .nickname(generateRandomNickname())
                .provider(response.getProvider())
                .providerId(response.getProviderId())
                .isDeleted(false)
                .build();

        roleService.assignDefaultRole(users);

        userRepository.save(users);

        log.info("소셜 회원가입 성공 email: {}, 닉네임: {}, provider: {}", users.getEmail(), users.getNickname(), users.getProvider());

        return users;

    }

    private String generateRandomNickname() {
        String alphabet = "abcdefghijklmnopqrstuvwxyz";
        Random random = new Random();
        int randomLength = random.nextInt(5) + 6;
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < randomLength; i++) {
            int index = random.nextInt(alphabet.length());
            sb.append(alphabet.charAt(index));
        }

        return sb.toString();
    }

    private void setAccessToken(OAuth2AccessToken oAuth2AccessToken, Users users) {
        String accessToken = oAuth2AccessToken.getTokenValue();

        ValueOperations<String, Object> valueOperations = redisTemplate.opsForValue();
        AccessToken socialAccessToken = new AccessToken().updateAccessToken(users.getId(), accessToken);
        valueOperations.set(String.format("%d_%s_accessToken", users.getId(), users.getProviderId()), socialAccessToken);
    }

}
