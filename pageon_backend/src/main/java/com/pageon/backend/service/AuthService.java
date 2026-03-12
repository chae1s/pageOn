package com.pageon.backend.service;

import com.pageon.backend.dto.response.JwtTokenResponse;
import com.pageon.backend.dto.response.ReissuedTokenResponse;
import com.pageon.backend.dto.request.TempCodeRequest;
import com.pageon.backend.dto.token.TokenInfo;
import com.pageon.backend.entity.User;
import com.pageon.backend.common.enums.RoleType;
import com.pageon.backend.exception.CustomException;
import com.pageon.backend.exception.ErrorCode;
import com.pageon.backend.repository.UserRepository;
import com.pageon.backend.security.JwtProvider;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final JwtProvider jwtProvider;
    private final RedisTemplate<String, Object> redisTemplate;
    private final UserRepository userRepository;


    @Transactional(readOnly = true)
    public ReissuedTokenResponse reissueToken(HttpServletRequest request, HttpServletResponse response) {
        log.info("access token 만료, refresh token으로 새로운 access token 발급");
        String refreshToken = extractRefreshToken(request);

        if (refreshToken == null) {
            throw new CustomException(ErrorCode.REFRESH_TOKEN_NOT_FOUND);
        }

        TokenInfo tokenInfo = (TokenInfo) redisTemplate.opsForValue().get(refreshToken);
        if (tokenInfo == null) {
            throw new CustomException(ErrorCode.INVALID_TOKEN);
        }

        User user = userRepository.findByIdAndDeleted(tokenInfo.getUserId(), false).orElseThrow(
                () -> new CustomException(ErrorCode.USER_NOT_FOUND)
        );

        List<RoleType> roleTypes = user.getUserRoles().stream().map(userRole -> userRole.getRole().getRoleType()).toList();

        // 새로운 accessToken 발급
        String accessToken = jwtProvider.generateAccessToken(tokenInfo.getEmail(), roleTypes);

        response.setHeader("Authorization", "Bearer " + accessToken);


        return new ReissuedTokenResponse(accessToken, true);

    }

    private String extractRefreshToken(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();

        if (cookies == null) return null;

        for (Cookie cookie : cookies) {
            if (cookie.getName().equals("refreshToken")) {
                return cookie.getValue();

            }
        }
        return null;

    }

    public JwtTokenResponse exchangeCode(HttpServletResponse response, TempCodeRequest tempCodeRequest) {
        String redisKey = String.format("user:oauth:code:%d", tempCodeRequest.getUserId());

        String tempCode = (String) redisTemplate.opsForValue().getAndDelete(redisKey);

        if (!tempCodeRequest.getTempCode().equals(tempCode)) {
            throw new CustomException(ErrorCode.INVALID_TEMP_CODE);
        }
        log.info("userId: {}", tempCodeRequest.getUserId());

        User user = userRepository.findWithRolesById(tempCodeRequest.getUserId()).orElseThrow(
                () -> new CustomException(ErrorCode.USER_NOT_FOUND)
        );

        List<RoleType> roleTypes = user.getUserRoles().stream()
                .map(userRole -> userRole.getRole().getRoleType())
                .collect(Collectors.toList());

        String accessToken = jwtProvider.generateAccessToken(user.getEmail(), roleTypes);
        String refreshToken = jwtProvider.generateRefreshToken(user.getEmail());

        List<String> userRoles = new ArrayList<>();
        for (RoleType roleType : roleTypes) {
            userRoles.add(roleType.toString());
        }

        jwtProvider.sendTokens(response, accessToken, refreshToken);
        setRefreshToken(user, refreshToken);

        return new JwtTokenResponse(true, accessToken, user.getOAuthProvider(), userRoles);

    }

    private void setRefreshToken(User users, String refreshToken) {
        ValueOperations<String, Object> valueOperations = redisTemplate.opsForValue();

        // refresh token 저장
        TokenInfo tokenInfo = new TokenInfo().updateTokenInfo(users.getId(), users.getEmail());
        valueOperations.set(refreshToken, tokenInfo);
    }

}
