package com.pageon.backend.service;

import com.pageon.backend.dto.request.FindPasswordRequest;
import com.pageon.backend.dto.request.LoginRequest;
import com.pageon.backend.dto.request.SignupRequest;
import com.pageon.backend.dto.request.UserUpdateRequest;
import com.pageon.backend.dto.response.JwtTokenResponse;
import com.pageon.backend.dto.response.UserInfoResponse;
import com.pageon.backend.dto.token.AccessToken;
import com.pageon.backend.dto.token.TokenInfo;
import com.pageon.backend.entity.Users;
import com.pageon.backend.common.enums.OAuthProvider;
import com.pageon.backend.exception.CustomException;
import com.pageon.backend.exception.ErrorCode;
import com.pageon.backend.repository.UserRepository;
import com.pageon.backend.security.JwtProvider;
import com.pageon.backend.security.PrincipalUser;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;
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
    private final RedisTemplate<String, Object> redisTemplate;
    private final RestTemplate restTemplate;

    @Value("${spring.security.oauth2.client.registration.naver.client-id}")
    private String naverClientId;

    @Value("${spring.security.oauth2.client.registration.naver.client-secret}")
    private String naverClientSecret;

    @Transactional
    public void signup(SignupRequest request) {
        Users users = createUser(request);

        roleService.assignDefaultRole(users);

        userRepository.save(users);

        log.info("이메일 회원가입 성공 email: {}, 닉네임: {}, provider: {}", users.getEmail(), users.getNickname(), users.getOAuthProvider());
    }


    private Users createUser(SignupRequest request) {

        return Users.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .nickname(request.getNickname())
                .oAuthProvider(OAuthProvider.EMAIL)
                .isDeleted(false)
                .terms_agreed(request.getTermsAgreed())
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
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginDto.getEmail(), loginDto.getPassword()
                )
        );

        PrincipalUser principalUser = (PrincipalUser) authentication.getPrincipal();

        // 로그인 시 토큰 생성
        String accessToken = jwtProvider.generateAccessToken(principalUser.getUsername(), principalUser.getRoleType());
        String refreshToken = jwtProvider.generateRefreshToken(principalUser.getUsername());
        if (accessToken == null || refreshToken == null) {
            throw new CustomException(ErrorCode.TOKEN_GENERATION_FAILED);
        }

        try {
            // refresh token 저장
            TokenInfo tokenInfo = new TokenInfo().updateTokenInfo(principalUser.getId(), principalUser.getUsername());
            redisTemplate.opsForValue().set(refreshToken, tokenInfo, Duration.ofDays(180));
        } catch (Exception e) {
            throw new CustomException(ErrorCode.REDIS_CONNECTION_FAILED);
        }

        JwtTokenResponse jwtTokenResponse = new JwtTokenResponse(true, accessToken, principalUser.getUsers().getOAuthProvider());

        jwtProvider.sendTokens(response, accessToken, refreshToken);


        return jwtTokenResponse;

    }

    public void logout(PrincipalUser principalUser, HttpServletRequest request, HttpServletResponse response) {
        Users users = userRepository.findByIdAndIsDeletedFalse(principalUser.getId()).orElseThrow(
                () -> new CustomException(ErrorCode.USER_NOT_FOUND)
        );

        deleteToken(getRefreshToken(request), users);

        Cookie cookie = new Cookie("refreshToken", null);
        cookie.setMaxAge(0);
        response.addCookie(cookie);
    }

    private String getRefreshToken(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("refreshToken".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }

        throw new CustomException(ErrorCode.REFRESH_TOKEN_NOT_FOUND);
    }

    private void deleteToken(String refreshToken, Users users) {

        TokenInfo tokenInfo = (TokenInfo) redisTemplate.opsForValue().get(refreshToken);

        if (tokenInfo.getUserId().equals(users.getId())) {
            redisTemplate.delete(refreshToken);
        }
    }

    @Transactional
    public Map<String, String> passwordFind(FindPasswordRequest passwordDto) {
        Map<String, String> result = new HashMap<>();
        Optional<Users> optionalUsers = userRepository.findByEmailAndIsDeletedFalse(passwordDto.getEmail());
        if (optionalUsers.isPresent()) {
            Users user = optionalUsers.get();
            if (user.getOAuthProvider() == OAuthProvider.EMAIL) {
                // provider가 email일 때, 임시 비밀번호 생성 후 db에 저장
                String tempPassword = generateRandomPassword();
                user.updatePassword(passwordEncoder.encode(tempPassword));
                // 임시 비밀번호를 담은 메일 발송
                mailService.sendTemporaryPassword(user.getEmail(), tempPassword);
                result.put("type", "email");
                result.put("message", "임시 비밀번호가 메일로 발송되었습니다.");
            } else {
                result.put("type", "social");
                result.put("message", String.format("%s로 회원가입된 이메일입니다.", user.getOAuthProvider()));
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

    public UserInfoResponse getMyInfo(PrincipalUser principalUser) {
        Users users = userRepository.findByEmailAndIsDeletedFalse(
                principalUser.getUsername()).orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND)
        );

        return UserInfoResponse.fromEntity(users);
    }

    public boolean checkPassword(Long id, String password) {
        Users users = userRepository.findByIdAndIsDeletedFalse(id).orElseThrow(
                () -> new CustomException(ErrorCode.USER_NOT_FOUND)
        );

        return passwordEncoder.matches(password, users.getPassword());
    }

    @Transactional
    public void updateProfile(Long id, UserUpdateRequest request) {
        Users users = userRepository.findByIdAndIsDeletedFalse(id).orElseThrow(
                () -> new CustomException(ErrorCode.USER_NOT_FOUND)
        );
        log.info(request.getNickname());
        if (request.getNickname() != null && !request.getNickname().isBlank()) {
            users.updateNickname(request.getNickname());
        }

        if (request.getPassword() != null && !request.getPassword().isBlank()) {
            users.updatePassword(validatePassword(request));
        }
    }

    private String validatePassword(UserUpdateRequest request) {
        if (!request.getPassword().matches("^(?=.*[a-zA-Z])(?=.*[0-9])(?=.*[!@#\\-?$%&^])[a-zA-Z0-9!@#\\-?$%&^]{8,}$")) {
            throw new CustomException(ErrorCode.PASSWORD_POLICY_VIOLATION);
        }

        return passwordEncoder.encode(request.getPassword());
    }

    @Transactional
    public Map<String, Object> deleteAccount(Long id, String password, HttpServletRequest request) {
        Users users = userRepository.findByIdAndIsDeletedFalse(id).orElseThrow(
                () -> new CustomException(ErrorCode.USER_NOT_FOUND)
        );

        if (users.getOAuthProvider() == OAuthProvider.EMAIL) {

            return deleteEmailAccount(users, password, request);
        } else {

            return deleteSocialAccount(users, request);
        }

    }

    // provider가 email일 때 계정 삭제 메소드
    private Map<String, Object> deleteEmailAccount(Users users, String password, HttpServletRequest request) {
        log.info("이메일 계정 삭제");
        if (passwordEncoder.matches(password, users.getPassword())) {
            // 회원 탈퇴
            return softDeleteAccount(users, "계정이 삭제되었습니다.", null, request);
        } else {
            // 에러 메세지 전송
            return Map.of(
                    "isDeleted", false,
                    "message", "비밀번호가 일치하지 않습니다."
            );
        }
    }

    // provider가 email이 아닐때 즉, 소셜로그인일 때 계정 삭제 메소드
    private Map<String, Object> deleteSocialAccount(Users users, HttpServletRequest request) {
        log.info("소셜 계정 삭제");
        String redisKey = String.format("%d_%s_accessToken", users.getId(), users.getProviderId());
        AccessToken accessToken = (AccessToken) redisTemplate.opsForValue().get(redisKey);
        switch (users.getOAuthProvider()) {
            case KAKAO -> {
                unlinkKakao(accessToken.getAccessToken());
                return softDeleteAccount(users, "카카오 계정이 삭제되었습니다.", redisKey, request);
            }
            case NAVER -> {
                unlinkNaver(accessToken.getAccessToken());
                return softDeleteAccount(users, "네이버 계정이 삭제되었습니다.", redisKey, request);
            }
            case GOOGLE -> {
                unlinkGoogle(accessToken.getAccessToken());
                return softDeleteAccount(users, "구글 계정이 삭제되었습니다.", redisKey, request);
            }
            default -> throw new CustomException(ErrorCode.OAUTH_PROVIDER_MISMATCH);
        }
    }

    // 삭제 계정 DB 변경
    private Map<String, Object> softDeleteAccount(Users users, String message, String redisKey, HttpServletRequest request) {
        // 회원 탈퇴
        users.deleteEmail(String.format("delete_%s_%d", users.getEmail(), users.getId()));
        users.updateNickname(String.format("delete_%s_%d", users.getNickname(), users.getId()));

        // 본인인증 데이터 제거
        users.updateIdentityVerification(null, null, null, null, null, false, null);
        if (users.getOAuthProvider() != OAuthProvider.EMAIL) {
            users.deleteProviderId(String.format("delete_%s_%d", users.getProviderId(), users.getId()));
            // 소셜로그인의 access token 삭제
            redisTemplate.delete(redisKey);
        }
        users.delete();

        // 로그인 시 받은 refresh token 삭제
        deleteToken(getRefreshToken(request), users);
        return Map.of(
                "isDeleted", true,
                "message", message
        );
    }

    // 카카오 연결 끊기
    private void unlinkKakao(String accessToken) {
        String url = "https://kapi.kakao.com/v1/user/unlink";

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + accessToken);

        sendUnlinkRequest(url, headers, "카카오");
    }

    // 네이버 연결 끊기
    private void unlinkNaver(String accessToken) {
        String url = String.format("https://nid.naver.com/oauth2.0/token?grant_type=delete&client_id=%s&client_secret=%s&access_token=%s&service_provider=NAVER",
                naverClientId, naverClientSecret, accessToken);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        sendUnlinkRequest(url, headers, "네이버");
    }

    // 구글 연결 끊기
    private void unlinkGoogle(String accessToken) {
        String url = String.format("https://oauth2.googleapis.com/revoke?token=%s", accessToken);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        sendUnlinkRequest(url, headers, "구글");
    }

    private void sendUnlinkRequest(String url, HttpHeaders headers, String provider) {
        HttpEntity<Void> request = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);

        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new CustomException(ErrorCode.OAUTH_UNLINK_FAILED);
        }
    }
}
