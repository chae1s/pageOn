package com.pageon.backend.service;

import com.pageon.backend.dto.SocialSignupDto;
import com.pageon.backend.dto.UserSignupDto;
import com.pageon.backend.entity.Users;
import com.pageon.backend.entity.enums.Provider;
import com.pageon.backend.entity.enums.Role;
import com.pageon.backend.repository.UserRepository;
import com.pageon.backend.security.CustomOAuth2User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

    public void signupSocial(CustomOAuth2User auth2User, SocialSignupDto signupDto) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        LocalDate birthDate = LocalDate.parse(signupDto.getBirthDate(), formatter);

        Users users = Users.builder()
                .email(signupDto.getEmail())
                .nickname(signupDto.getNickname())
                .birthDate(birthDate)
                .role(Role.ROLE_USER)
                .provider(auth2User.getProvider())
                .providerId(auth2User.getProviderId())
                .build();

        userRepository.save(users);

        log.info("소셜 회원가입 성공 email: {}, 닉네임: {}, provider: {}", users.getEmail(), users.getNickname(), users.getProvider());
    }

    public boolean isEmailDuplicate(String email) {
        log.info("이메일 중복 확인");
        return userRepository.existsByEmail(email);
    }

    public boolean isNicknameDuplicate(String nickname) {
        log.info("닉네임 중복 확인");
        return userRepository.existsByNickname(nickname);
    }
}
