package com.pageon.backend.service;

import com.pageon.backend.dto.JwtTokenResponse;
import com.pageon.backend.dto.TokenInfo;
import com.pageon.backend.entity.Users;
import com.pageon.backend.entity.enums.RoleType;
import com.pageon.backend.repository.UserRepository;
import com.pageon.backend.security.JwtProvider;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final JwtProvider jwtProvider;
    private final RedisTemplate<String, Object> redisTemplate;
    private final UserRepository userRepository;


    public JwtTokenResponse reissueToken(HttpServletRequest request, HttpServletResponse response) {
        String refreshToken = extractRefreshToken(request);

        if (refreshToken == null) {
            throw new RuntimeException("쿠키에 Refresh Token이 없습니다.");
        }

        TokenInfo tokenInfo = (TokenInfo) redisTemplate.opsForValue().get(refreshToken);
        if (tokenInfo == null) {
            throw new RuntimeException("유효하지 않은 Refresh Token입니다.");
        }
        // refreshToken에서 가져온 email
        String email = jwtProvider.getUsernameRefreshToken(refreshToken);

        Users users = userRepository.findByEmailAndIsDeletedFalse(email).orElseThrow(() -> new UsernameNotFoundException("존재하지 않는 사용자입니다."));
        if (!users.getId().equals(tokenInfo.getUserId())) {
            throw new RuntimeException("refresh token 소유자가 일치하지 않습니다.");
        }

        List<RoleType> roleTypes = users.getUserRoles().stream().map(userRole -> userRole.getRole().getRoleType()).toList();

        // 새로운 accessToken 발급
        String accessToken = jwtProvider.generateAccessToken(email, roleTypes);

        response.setHeader("Authorization", "Bearer " + accessToken);

        return new JwtTokenResponse(true, accessToken, users.getProvider());

    }

    private String extractRefreshToken(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();

        if (cookies != null) return null;

        for (Cookie cookie : cookies) {
            if (cookie.getName().equals("refreshToken")) {
                return cookie.getValue();

            }
        }
        return null;

    }

}
