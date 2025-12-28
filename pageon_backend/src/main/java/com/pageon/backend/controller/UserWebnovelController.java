package com.pageon.backend.controller;

import com.pageon.backend.common.enums.ContentType;
import com.pageon.backend.dto.response.ContentResponse;
import com.pageon.backend.dto.response.PageResponse;
import com.pageon.backend.dto.response.UserContentListResponse;
import com.pageon.backend.security.PrincipalUser;
import com.pageon.backend.service.InterestService;
import com.pageon.backend.service.UserWebnovelService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/webnovels")
@RequiredArgsConstructor
public class UserWebnovelController {
    private final UserWebnovelService userWebnovelService;
    private final InterestService interestService;

    @GetMapping("/{webnovelId}")
    public ResponseEntity<ContentResponse.Detail> getWebnovelById(@PathVariable Long webnovelId, @AuthenticationPrincipal PrincipalUser principalUser) {

        return ResponseEntity.ok(userWebnovelService.getWebnovelById(webnovelId, principalUser));
    }

    @GetMapping()
    public ResponseEntity<List<ContentResponse.Summary>> getWebnovels() {

        return ResponseEntity.ok(userWebnovelService.getWebnovels());
    }

    @GetMapping("/daily/{day}")
    public ResponseEntity<List<ContentResponse.Simple>> getWebnovelsByDay(@PathVariable String day) {

        return ResponseEntity.ok(userWebnovelService.getWebnovelsByDay(day));
    }

    @PostMapping("/{webnovelId}/interests")
    public ResponseEntity<Void> RegisterInterest(
            @AuthenticationPrincipal PrincipalUser principalUser, @PathVariable Long webnovelId
    ) {

        log.info("webnovel : {}", webnovelId);
        interestService.registerInterest(principalUser.getId(), webnovelId);

        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{webnovelId}/interests")
    public ResponseEntity<Void> DeleteInterest(
            @AuthenticationPrincipal PrincipalUser principalUser, @PathVariable Long webnovelId
    ) {
        interestService.deleteInterest(principalUser.getId(), webnovelId);

        return ResponseEntity.ok().build();
    }

    @GetMapping("/recent")
    public ResponseEntity<PageResponse<ContentResponse.Simple>> getRecentWebnovels(@PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {

        Page<ContentResponse.Simple> contents = userWebnovelService.getRecentWebnovels(pageable);

        return ResponseEntity.ok(new PageResponse<>(contents));
    }
}
