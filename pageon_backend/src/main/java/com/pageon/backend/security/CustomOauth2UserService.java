package com.pageon.backend.security;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pageon.backend.dto.oauth.KakaoSignupDto;
import com.pageon.backend.dto.oauth.NaverSignupDto;
import com.pageon.backend.dto.oauth.OAuth2Response;
import com.pageon.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomOauth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

        OAuth2User oAuth2User = super.loadUser(userRequest);

        log.info("OAuth2User Attributes: {}", oAuth2User.getAttributes().get("response"));

        String registrationId = userRequest.getClientRegistration().getRegistrationId();

        OAuth2Response oAuth2Response = null;

        if (registrationId.equals("kakao")) {
            log.info("kakao 로그인");
            oAuth2Response = new KakaoSignupDto(oAuth2User.getAttributes());
        } else if (registrationId.equals("naver")) {
            log.info("naver 로그인");
            Object response = oAuth2User.getAttributes().get("response");
            ObjectMapper mapper = new ObjectMapper();
            Map<String, Object> attribute = mapper.convertValue(response, new TypeReference<>() {});

            oAuth2Response = new NaverSignupDto(attribute);
        }

        return new CustomOAuth2User(oAuth2User, oAuth2Response);
    }
}
