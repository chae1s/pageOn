package com.pageon.backend.service;

import com.pageon.backend.dto.JwtDto;
import com.pageon.backend.dto.UserLoginRequestDto;
import com.pageon.backend.dto.UserSocialSignupDto;
import com.pageon.backend.dto.UserSignupDto;
import com.pageon.backend.entity.Users;
import com.pageon.backend.entity.enums.Provider;
import com.pageon.backend.entity.enums.Role;
import com.pageon.backend.repository.UserRepository;
import com.pageon.backend.security.CustomOAuth2User;
import com.pageon.backend.security.CustomUserDetails;
import com.pageon.backend.security.JwtProvider;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;
    private final AuthenticationManager authenticationManager;

    public void signup(UserSignupDto signupDto) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        LocalDate birthDate = LocalDate.parse(signupDto.getBirthDate(), formatter);

        Users users = Users.builder()
                .email(signupDto.getEmail())
                .password(passwordEncoder.encode(signupDto.getPassword()))
                .nickname(signupDto.getNickname())
                .birthDate(birthDate)
                .role(Role.ROLE_USER)
                .provider(Provider.EMAIL)
                .build();

        userRepository.save(users);

        log.info("이메일 회원가입 성공 email: {}, 닉네임: {}, provider: {}", users.getEmail(), users.getNickname(), users.getProvider());
    }

    public boolean isEmailDuplicate(String email) {
        log.info("이메일 중복 확인");
        return userRepository.existsByEmail(email);
    }

    public boolean isNicknameDuplicate(String nickname) {
        log.info("닉네임 중복 확인");
        return userRepository.existsByNickname(nickname);
    }

    public JwtDto login(UserLoginRequestDto loginDto, HttpServletResponse response) {
        boolean loginCheck = false;

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginDto.getEmail(), loginDto.getPassword()
                )
        );

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

        // 로그인 시 토큰 생성
        String accessToken = jwtProvider.generateAccessToken(userDetails.getId(), userDetails.getRole());
        String refreshToken = jwtProvider.generateRefreshToken(userDetails.getId());
        if (accessToken != null && refreshToken != null) {
            log.info("토큰 발급 완료");
            loginCheck = true;
        }

        JwtDto jwtDto = new JwtDto(loginCheck, accessToken);

        jwtProvider.sendTokens(response, accessToken, refreshToken);

        // refresh token 저장

        return jwtDto;

    }

    public void logout(HttpServletResponse response) {
        Cookie cookie = new Cookie("refreshToken", null);
        cookie.setMaxAge(0);
        response.addCookie(cookie);
    }


}
