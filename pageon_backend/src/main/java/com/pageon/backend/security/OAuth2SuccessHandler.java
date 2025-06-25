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

import java.io.IOException;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final UserRepository userRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {

        CustomOAuth2User oAuth2User = (CustomOAuth2User) authentication.getPrincipal();

        /* 이메일 로그인 사용자 정보 조회 */


        /* 소셜 로그인 사용자 정보 조회 */
        Provider provider = oAuth2User.getProvider();
        String providerId = oAuth2User.getProviderId();

        log.info("{} 로그인 성공", provider);
        Optional<Users> userOptional = userRepository.findByProviderAndProviderId(provider, providerId);

        if (userOptional.isPresent()) {
            response.sendRedirect("http://localhost:3000");
        } else {
            response.sendRedirect("http://localhost:3000/users/signup/social");
        }

    }
}
