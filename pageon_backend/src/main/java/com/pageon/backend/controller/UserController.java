package com.pageon.backend.controller;

import com.pageon.backend.common.enums.ContentType;
import com.pageon.backend.dto.request.*;
import com.pageon.backend.dto.response.ContentPageResponse;
import com.pageon.backend.dto.response.ContentSimpleResponse;
import com.pageon.backend.dto.response.JwtTokenResponse;
import com.pageon.backend.dto.response.UserInfoResponse;
import com.pageon.backend.security.PrincipalUser;
import com.pageon.backend.service.InterestService;
import com.pageon.backend.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final InterestService interestService;

    @PostMapping("/signup")
    public ResponseEntity<Void> signup(@Valid @RequestBody SignupRequest signupDto) {
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
    public ResponseEntity<Map<String, JwtTokenResponse>> login(@RequestBody LoginRequest loginDto, HttpServletResponse response) {
        log.info("로그인");
        JwtTokenResponse jwtTokenResponse = userService.login(loginDto, response);

        return ResponseEntity.ok(Map.of("success", jwtTokenResponse));
    }

    @GetMapping("/logout")
    public ResponseEntity<Void> logout(@AuthenticationPrincipal PrincipalUser principalUser, HttpServletRequest request, HttpServletResponse response) {

        userService.logout(principalUser, request, response);

        return ResponseEntity.ok().build();
    }

    @PostMapping("/find-password")
    public ResponseEntity<Map<String, String>> passwordFind(@RequestBody FindPasswordRequest passwordDto) {
        Map<String, String> result = userService.passwordFind(passwordDto);

        return ResponseEntity.ok(result);
    }

    @GetMapping("/me")
    public ResponseEntity<UserInfoResponse> getMyInfo(@AuthenticationPrincipal PrincipalUser principalUser) {

        return ResponseEntity.ok(userService.getMyInfo(principalUser));
    }

    @PostMapping("/check-password")
    public ResponseEntity<Map<String, Boolean>> checkPassword(@AuthenticationPrincipal PrincipalUser user, @RequestBody Map<String, String> body) {
        String password = body.get("password");
        boolean isCorrect = userService.checkPassword(user.getId(), password);

        return ResponseEntity.ok(Map.of("isCorrect", isCorrect));
    }

    @PatchMapping("/me")
    public ResponseEntity<Void> updateProfile(@AuthenticationPrincipal PrincipalUser principalUser, @RequestBody UserUpdateRequest request) {
        userService.updateProfile(principalUser.getId(), request);

        return ResponseEntity.ok().build();
    }

    @PostMapping("/withdraw")
    public ResponseEntity<Map<String, Object>> deleteAccount(
            @AuthenticationPrincipal PrincipalUser principalUser, @RequestBody UserDeleteRequest userDeleteRequest, HttpServletRequest request
    ) {

        log.info("탈퇴");
        return ResponseEntity.ok(userService.deleteAccount(principalUser.getId(), userDeleteRequest, request));
    }

    @GetMapping("/check-identity")
    public ResponseEntity<Boolean> checkIdentityVerification(@AuthenticationPrincipal PrincipalUser principalUser){

        return ResponseEntity.ok(userService.checkIdentityVerification(principalUser));
    }

    @GetMapping("/interests")
    public ResponseEntity<ContentPageResponse<ContentSimpleResponse>> getInterests(
            @AuthenticationPrincipal PrincipalUser principalUser, @PageableDefault Pageable pageable, @RequestParam("sort") String sort, @RequestParam(value = "type", required = false) ContentType contentType
    ){

        log.info("My Interests request received. Type: [{}], Sort: [{}], Page: {}, size: {}",
                contentType, sort, pageable.getPageNumber(), pageable.getPageSize());
        Page<ContentSimpleResponse> contentSimpleResponses = interestService.getInterestedContents(principalUser.getId(), contentType, sort, pageable);

        return ResponseEntity.ok(new ContentPageResponse<>(contentSimpleResponses));
    }
}
