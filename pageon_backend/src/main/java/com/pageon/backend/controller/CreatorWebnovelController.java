package com.pageon.backend.controller;

import com.pageon.backend.dto.request.WebnovelCreateRequest;
import com.pageon.backend.dto.response.CreatorWebnovelListResponse;
import com.pageon.backend.dto.response.CreatorWebnovelResponse;
import com.pageon.backend.security.PrincipalUser;
import com.pageon.backend.service.CreatorWebnovelService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/creators/webnovels")
@RequiredArgsConstructor
public class CreatorWebnovelController {

    private final CreatorWebnovelService webnovelService;

    @PostMapping()
    public ResponseEntity<Void> createWebnovel(@AuthenticationPrincipal PrincipalUser principalUser, @Valid @ModelAttribute WebnovelCreateRequest webnovelCreateRequest) {
        webnovelService.createWebnovel(principalUser, webnovelCreateRequest);

        return ResponseEntity.ok().build();
    }

    @GetMapping("/{webnovelId}")
    public ResponseEntity<CreatorWebnovelResponse> getWebnovelById(@AuthenticationPrincipal PrincipalUser principalUser, @PathVariable Long webnovelId) {

        return ResponseEntity.ok(webnovelService.getWebnovelById(principalUser, webnovelId));
    }

    @GetMapping()
    public ResponseEntity<List<CreatorWebnovelListResponse>> getMyWebnovels(@AuthenticationPrincipal PrincipalUser principalUser) {

        return ResponseEntity.ok(webnovelService.getMyWebnovels(principalUser));
    }


}
