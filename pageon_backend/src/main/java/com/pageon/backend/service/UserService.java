package com.pageon.backend.service;

import com.pageon.backend.dto.*;
import com.pageon.backend.dto.oauth.OAuthUserInfoResponse;
import com.pageon.backend.entity.Role;
import com.pageon.backend.entity.UserRole;
import com.pageon.backend.entity.Users;
import com.pageon.backend.entity.enums.Provider;
import com.pageon.backend.entity.enums.RoleType;
import com.pageon.backend.repository.RoleRepository;
import com.pageon.backend.repository.UserRepository;
import com.pageon.backend.security.CustomUserDetails;
import com.pageon.backend.security.JwtProvider;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Random;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;
    private final AuthenticationManager authenticationManager;
    private final MailService mailService;
    private final RoleService roleService;

    @Transactional
    public void signup(SignupRequest request) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        LocalDate birthDate = LocalDate.parse(request.getBirthDate(), formatter);

        Users users = createUser(request);

        roleService.assignDefaultRole(users);

        userRepository.save(users);

        log.info("이메일 회원가입 성공 email: {}, 닉네임: {}, provider: {}", users.getEmail(), users.getNickname(), users.getProvider());
    }


    private Users createUser(SignupRequest request) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        LocalDate birthDate = LocalDate.parse(request.getBirthDate(), formatter);

        return Users.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .nickname(request.getNickname())
                .birthDate(birthDate)
                .provider(Provider.EMAIL)
                .build();

    }


    public boolean isEmailDuplicate(String email) {
        log.info("이메일 중복 확인");
        return userRepository.existsByEmail(email);
    }

    public boolean isNicknameDuplicate(String nickname) {
        log.info("닉네임 중복 확인");
        return userRepository.existsByNickname(nickname);
    }

    public JwtTokenResponse login(LoginRequest loginDto, HttpServletResponse response) {
        boolean loginCheck = false;

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginDto.getEmail(), loginDto.getPassword()
                )
        );

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

        // 로그인 시 토큰 생성
        String accessToken = jwtProvider.generateAccessToken(userDetails.getId(), userDetails.getRoleType());
        String refreshToken = jwtProvider.generateRefreshToken(userDetails.getId());
        if (accessToken != null && refreshToken != null) {
            log.info("토큰 발급 완료");
            loginCheck = true;
        }

        JwtTokenResponse jwtTokenResponse = new JwtTokenResponse(loginCheck, accessToken);

        jwtProvider.sendTokens(response, accessToken, refreshToken);

        // refresh token 저장

        return jwtTokenResponse;

    }

    public void logout(HttpServletResponse response) {
        Cookie cookie = new Cookie("refreshToken", null);
        cookie.setMaxAge(0);
        response.addCookie(cookie);
    }

    @Transactional
    public Map<String, String> passwordFind(FindPasswordRequest passwordDto) {
        Map<String, String> result = new HashMap<>();
        Optional<Users> optionalUsers = userRepository.findByEmail(passwordDto.getEmail());
        if (optionalUsers.isPresent()) {
            Users user = optionalUsers.get();
            if (user.getProvider() == Provider.EMAIL) {
                // provider가 email일 때, 임시 비밀번호 생성 후 db에 저장
                String tempPassword = generateRandomPassword();
                user.updatePassword(passwordEncoder.encode(tempPassword));
                // 임시 비밀번호를 담은 메일 발송
                mailService.sendTemporaryPassword(user.getEmail(), tempPassword);
                result.put("type", "email");
                result.put("message", "임시 비밀번호가 메일로 발송되었습니다.");
            } else {
                result.put("type", "social");
                result.put("message", String.format("%s로 회원가입된 이메일입니다.", user.getProvider()));
            }
        } else {
            result.put("type", "noUser");
            result.put("message", "회원가입되지 않은 이메일입니다.");
        }

        return result;
    }

    private String generateRandomPassword() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz1234567890";
        Random random = new Random();

        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < 8; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }

        return sb.toString();
    }

}
