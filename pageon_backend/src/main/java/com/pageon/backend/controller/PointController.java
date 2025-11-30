package com.pageon.backend.controller;

import com.pageon.backend.dto.response.PageResponse;
import com.pageon.backend.dto.response.PointTransactionResponse;
import com.pageon.backend.security.PrincipalUser;
import com.pageon.backend.service.PointTransactionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/points")
@RequiredArgsConstructor
public class PointController {

    private final PointTransactionService pointTransactionService;

    @GetMapping("/history")
    public ResponseEntity<PageResponse<PointTransactionResponse>> getPointHistory (
            @AuthenticationPrincipal PrincipalUser principalUser,
            @RequestParam("type") String transactionType,
            @PageableDefault(size = 20) Pageable pageable
    ) {
        Page<PointTransactionResponse> pointTransactionResponses = pointTransactionService.getPointHistory(principalUser.getId(), transactionType, pageable);

        return ResponseEntity.ok(new PageResponse<>(pointTransactionResponses));
    }
}
