package com.pageon.backend.controller;

import com.pageon.backend.dto.response.ContentResponse;
import com.pageon.backend.dto.response.PageResponse;
import com.pageon.backend.service.RankingService;
import com.pageon.backend.service.RecommendationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/recommendation")
@RequiredArgsConstructor
public class RecommendationController {

    private final RecommendationService recommendationService;
    private final RankingService rankingService;

    @GetMapping("/recent")
    public ResponseEntity<PageResponse<ContentResponse.Simple>> getRecentContents(
            @RequestParam String contentType,
            @PageableDefault(size = 60, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {

        log.info("getRecentContents");
        Page<ContentResponse.Simple> contents = recommendationService.getRecentContents(contentType, pageable);

        return ResponseEntity.ok(new PageResponse<>(contents));
    }

    @GetMapping("/masterpiece")
    public ResponseEntity<PageResponse<ContentResponse.Simple>> getMasterpiecesContents(
            @RequestParam("contentType") String contentType,
            @PageableDefault(size = 60, sort = "viewCount", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        Page<ContentResponse.Simple> contents = recommendationService.getMasterpiecesContents(contentType, pageable);
        return ResponseEntity.ok(new PageResponse<>(contents));
    }

    @GetMapping("/by-keyword")
    public ResponseEntity<Map<String, Object>> getRecommendKeywordContents(
            @RequestParam String contentType,
            @PageableDefault(size = 60, sort = "viewCount", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        Map<String, Object> result = recommendationService.getRecommendKeywordContents(contentType, pageable);

        return ResponseEntity.ok(result);
    }

    @GetMapping("/hourly-ranking")
    public ResponseEntity<List<ContentResponse.Simple>> getHourlyRankingContents(
            @RequestParam String contentType
    ) {

        List<ContentResponse.Simple> contents = rankingService.getHourlyRankingContents(contentType);

        return ResponseEntity.ok(contents);
    }
}
