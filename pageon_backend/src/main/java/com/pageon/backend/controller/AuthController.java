package com.pageon.backend.controller;

import com.pageon.backend.dto.JwtTokenResponse;
import com.pageon.backend.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(("/api/auth"))
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/reissue")
    public ResponseEntity<JwtTokenResponse> reissueToken(HttpServletRequest request, HttpServletResponse response) {

        return ResponseEntity.ok(authService.reissueToken(request, response));
    }
}
