package com.pageon.backend.controller;

import com.pageon.backend.dto.request.WebnovelCreateRequest;
import com.pageon.backend.security.PrincipalUser;
import com.pageon.backend.service.WebnovelService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/creators/webnovels")
@RequiredArgsConstructor
public class WebnovelController {

    private final WebnovelService webnovelService;

    @PostMapping()
    public ResponseEntity<Void> createWebnovel(@AuthenticationPrincipal PrincipalUser principalUser, @Valid @ModelAttribute WebnovelCreateRequest webnovelCreateRequest) {
        webnovelService.createWebnovel(principalUser, webnovelCreateRequest);

        return ResponseEntity.ok().build();
    }
}
