package com.pageon.backend.security;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pageon.backend.dto.UserSocialSignupDto;
import com.pageon.backend.dto.oauth.GoogleSignupDto;
import com.pageon.backend.dto.oauth.KakaoSignupDto;
import com.pageon.backend.dto.oauth.NaverSignupDto;
import com.pageon.backend.dto.oauth.OAuth2Response;
import com.pageon.backend.entity.Users;
import com.pageon.backend.entity.enums.Role;
import com.pageon.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.Optional;
import java.util.Random;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomOauth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

        OAuth2User oAuth2User = super.loadUser(userRequest);

        log.info("OAuth2User Attributes: {}", oAuth2User.getAttributes());

        String registrationId = userRequest.getClientRegistration().getRegistrationId();

        OAuth2Response oAuth2Response = null;

        if (registrationId.equals("kakao")) {
            log.info("kakao 로그인");
            oAuth2Response = new KakaoSignupDto(oAuth2User.getAttributes());
            existingUser(oAuth2Response);
        } else if (registrationId.equals("naver")) {
            log.info("naver 로그인");
            Object response = oAuth2User.getAttributes().get("response");
            ObjectMapper mapper = new ObjectMapper();
            Map<String, Object> attribute = mapper.convertValue(response, new TypeReference<>() {});

            oAuth2Response = new NaverSignupDto(attribute);
            existingUser(oAuth2Response);
        } else if (registrationId.equals("google")) {
            log.info("google 로그인");
            oAuth2Response = new GoogleSignupDto(oAuth2User.getAttributes());

            existingUser(oAuth2Response);
        }

        return new CustomOAuth2User(oAuth2User, oAuth2Response);
    }

    private void existingUser(OAuth2Response response) {
        log.info(response.getEmail());

        Optional<Users> user = userRepository.findByProviderAndProviderId(response.getProvider(), response.getProviderId());

        if (user.isPresent()) {
            return;
        }

        signupSocial(response);

    }

    private Users signupSocial(OAuth2Response response) {

        Users users = Users.builder()
                .email(response.getEmail())
                .nickname(generateRandomNickname())
                .role(Role.ROLE_USER)
                .provider(response.getProvider())
                .providerId(response.getProviderId())
                .build();

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
}
