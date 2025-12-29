package com.pageon.backend.controller;

import com.pageon.backend.common.enums.ContentType;
import com.pageon.backend.dto.response.ContentResponse;
import com.pageon.backend.dto.response.PageResponse;
import com.pageon.backend.dto.response.UserContentListResponse;
import com.pageon.backend.security.PrincipalUser;
import com.pageon.backend.service.InterestService;
import com.pageon.backend.service.UserWebtoonService;
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
@RequestMapping("/api/webtoons")
@RequiredArgsConstructor
public class UserWebtoonController {

    private final UserWebtoonService userWebtoonService;
    private final InterestService interestService;

    @GetMapping("/{webtoonId}")
    public ResponseEntity<ContentResponse.Detail> getWebtoonById(@PathVariable Long webtoonId, @AuthenticationPrincipal PrincipalUser principalUser) {

        return ResponseEntity.ok(userWebtoonService.getWebtoonById(webtoonId, principalUser));
    }

    @GetMapping()
    public ResponseEntity<List<UserContentListResponse>> getWebtoons() {

        return ResponseEntity.ok(userWebtoonService.getWebtoons());
    }

    @GetMapping("/daily/{day}")
    public ResponseEntity<List<ContentResponse.Simple>> getWebtoonsByDay(@PathVariable String day) {

        return ResponseEntity.ok(userWebtoonService.getWebtoonsByDay(day));
    }

    @PostMapping("/{webtoonId}/interests")
    public ResponseEntity<Void> registerInterest(
            @AuthenticationPrincipal PrincipalUser principalUser, @PathVariable Long webtoonId
    ) {
        log.info("WEBTOON {} 관심 등록",  webtoonId);
        interestService.registerInterest(principalUser.getId(), webtoonId);

        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{webtoonId}/interests")
    public ResponseEntity<Void> deleteInterest(
            @AuthenticationPrincipal PrincipalUser principalUser, @PathVariable Long webtoonId
    ) {
        interestService.deleteInterest(principalUser.getId(), webtoonId);

        return ResponseEntity.ok().build();
    }

    @GetMapping("/recent")
    public ResponseEntity<PageResponse<ContentResponse.Simple>> getRecentWebtoons(@PageableDefault(size = 60, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {

        Page<ContentResponse.Simple> contents = userWebtoonService.getRecentWebtoons(pageable);

        return ResponseEntity.ok(new PageResponse<>(contents));
    }

}
