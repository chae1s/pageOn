package com.pageon.backend.security;

import com.pageon.backend.dto.token.TokenInfo;
import com.pageon.backend.entity.Users;
import com.pageon.backend.common.enums.Provider;
import com.pageon.backend.common.enums.RoleType;
import com.pageon.backend.repository.UserRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final UserRepository userRepository;
    private final JwtProvider jwtProvider;
    private final RedisTemplate<String, Object> redisTemplate;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {

        PrincipalUser principalUser = (PrincipalUser) authentication.getPrincipal();


        /* 소셜 로그인 사용자 정보 조회 */
        Provider provider = principalUser.getProvider();
        String providerId = principalUser.getProviderId();


        log.info("{} 로그인 성공", provider);

        Users user = userRepository.findWithRolesByProviderAndProviderId(provider, providerId).orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        List<RoleType> roleTypes = user.getUserRoles().stream().map(userRole -> userRole.getRole().getRoleType()).collect(Collectors.toList());
        log.info(principalUser.getName());
        String accessToken = jwtProvider.generateAccessToken(user.getEmail(), roleTypes);
        String refreshToken = jwtProvider.generateRefreshToken(user.getEmail());

        log.info("소셜로그인 토큰 발행");

        jwtProvider.sendTokens(response, accessToken, refreshToken);
        setRefreshToken(user, refreshToken);
        String redirectUrl = UriComponentsBuilder
                .fromUriString("http://localhost:3000/oauth/callback")
                .queryParam("accessToken", accessToken)
                .queryParam("provider", provider)
                .build()
                .toUriString();

        response.sendRedirect(redirectUrl);

    }

    private void setRefreshToken(Users users, String refreshToken) {
        ValueOperations<String, Object> valueOperations = redisTemplate.opsForValue();

        // refresh token 저장
        TokenInfo tokenInfo = new TokenInfo().updateTokenInfo(users.getId(), users.getEmail());
        valueOperations.set(refreshToken, tokenInfo);
    }

}
