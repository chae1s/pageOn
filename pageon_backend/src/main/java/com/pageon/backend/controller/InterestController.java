package com.pageon.backend.controller;

import com.pageon.backend.dto.response.InterestContentResponse;
import com.pageon.backend.dto.response.PageResponse;
import com.pageon.backend.security.PrincipalUser;
import com.pageon.backend.service.InterestService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/interests")
@RequiredArgsConstructor
public class InterestController {

    private final InterestService interestService;

    @PostMapping("/{contentId}")
    public ResponseEntity<Void> RegisterInterest(
            @AuthenticationPrincipal PrincipalUser principalUser,
            @PathVariable Long contentId
    ) {
        interestService.registerInterest(principalUser.getId(), contentId);

        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{contentId}")
    public ResponseEntity<Void> DeleteInterest(
            @AuthenticationPrincipal PrincipalUser principalUser,
            @PathVariable Long contentId
    ) {
        interestService.deleteInterest(principalUser.getId(), contentId);

        return ResponseEntity.ok().build();
    }


}
