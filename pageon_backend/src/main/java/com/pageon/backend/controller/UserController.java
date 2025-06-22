package com.pageon.backend.controller;

import com.pageon.backend.dto.SocialSignupDto;
import com.pageon.backend.dto.UserSignupDto;
import com.pageon.backend.security.CustomOAuth2User;
import com.pageon.backend.security.CustomOauth2UserService;
import com.pageon.backend.service.UserService;
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

    @PostMapping("/signup/social")
    public ResponseEntity<Void> socialSignup(@Valid @RequestBody SocialSignupDto signupDto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        log.info(authentication.getName());
        CustomOAuth2User oAuth2User = (CustomOAuth2User) authentication.getPrincipal();
        userService.signupSocial(oAuth2User, signupDto);

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




}
