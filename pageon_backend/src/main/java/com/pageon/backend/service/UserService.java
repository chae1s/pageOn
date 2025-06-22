package com.pageon.backend.service;

import com.pageon.backend.dto.UserSignupDto;
import com.pageon.backend.entity.Users;
import com.pageon.backend.entity.enums.Role;
import com.pageon.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
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
                .build();

        userRepository.save(users);

        log.info("회원가입 완료 email:{}, nickname: {}", users.getEmail(), users.getNickname());
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
