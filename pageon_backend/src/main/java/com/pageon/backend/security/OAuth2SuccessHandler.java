package com.pageon.backend.security;

import com.pageon.backend.entity.Users;
import com.pageon.backend.entity.enums.Provider;
import com.pageon.backend.repository.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final UserRepository userRepository;
    private final JwtProvider jwtProvider;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {

        CustomOAuth2User oAuth2User = (CustomOAuth2User) authentication.getPrincipal();


        /* 소셜 로그인 사용자 정보 조회 */
        Provider provider = oAuth2User.getProvider();
        String providerId = oAuth2User.getProviderId();


        log.info("{} 로그인 성공", provider);

        Users user = userRepository.findByProviderAndProviderId(provider, providerId).orElse(null);

        if (user != null) {
            log.info(oAuth2User.getName());
            String accessToken = jwtProvider.generateAccessToken(user.getId(), user.getRole());
            String refreshToken = jwtProvider.generateRefreshToken(user.getId());

            log.info("소셜로그인 토큰 발행");

            jwtProvider.sendTokens(response, accessToken, refreshToken);
            String redirectUrl = UriComponentsBuilder
                    .fromUriString("http://localhost:3000/oauth/callback")
                    .queryParam("accessToken", accessToken)
                    .build()
                    .toUriString();

            response.sendRedirect(redirectUrl);

        } else {
            response.sendRedirect("http://localhost:3000/users/signup/social");
        }

    }
}
