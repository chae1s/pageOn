package com.pageon.backend.controller;

import com.pageon.backend.dto.JwtDto;
import com.pageon.backend.dto.UserLoginRequestDto;
import com.pageon.backend.dto.UserSocialSignupDto;
import com.pageon.backend.dto.UserSignupDto;
import com.pageon.backend.security.CustomOAuth2User;
import com.pageon.backend.security.CustomOauth2UserService;
import com.pageon.backend.service.UserService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final CustomOauth2UserService customOauth2UserService;

    @PostMapping("/signup")
    public ResponseEntity<Void> signup(@Valid @RequestBody UserSignupDto signupDto) {
        userService.signup(signupDto);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/check-email")
    public ResponseEntity<Map<String, Boolean>> checkEmail(@RequestParam("email") String email) {
        boolean isEmailDuplicate = userService.isEmailDuplicate(email);

        return ResponseEntity.ok(Map.of("isEmailDuplicate", isEmailDuplicate));
    }

    @GetMapping("/check-nickname")
    public ResponseEntity<Map<String, Boolean>> checkNickname(@RequestParam("nickname") String nickname) {
        boolean isNicknameDuplicate = userService.isNicknameDuplicate(nickname);

        return ResponseEntity.ok(Map.of("isNicknameDuplicate", isNicknameDuplicate));
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, JwtDto>> login(@RequestBody UserLoginRequestDto loginDto, HttpServletResponse response) {
        log.info("로그인");
        JwtDto jwtDto = userService.login(loginDto, response);

        return ResponseEntity.ok(Map.of("success", jwtDto));
    }

    @GetMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletResponse response) {

        userService.logout(response);

        return ResponseEntity.ok().build();
    }



}
