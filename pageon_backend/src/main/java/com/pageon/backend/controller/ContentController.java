package com.pageon.backend.controller;

import com.pageon.backend.dto.response.ContentResponse;
import com.pageon.backend.dto.response.PageResponse;
import com.pageon.backend.service.ContentService;
import com.pageon.backend.service.RankingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/contents")
@RequiredArgsConstructor
public class ContentController {

    private final ContentService contentService;
    private final RankingService rankingService;

    @GetMapping("/daily/{day}")
    public ResponseEntity<List<ContentResponse.Simple>> getDailyContents(
            @PathVariable String day, @RequestParam String contentType
    ) {

        List<ContentResponse.Simple> contents = contentService.getContentsByDate(day, contentType);

        return ResponseEntity.ok(contents);
    }


    @GetMapping("/recent")
    public ResponseEntity<PageResponse<ContentResponse.Simple>> getRecentContents(
            @RequestParam String contentType,
            @PageableDefault(size = 60, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {

        log.info("getRecentContents");
        Page<ContentResponse.Simple> contents = contentService.getRecentContents(contentType, pageable);

        return ResponseEntity.ok(new PageResponse<>(contents));
    }

    @GetMapping("/masterpiece")
    public ResponseEntity<PageResponse<ContentResponse.Simple>> getMasterpiecesContents(
            @RequestParam("contentType") String contentType,
            @PageableDefault(size = 60, sort = "viewCount", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        Page<ContentResponse.Simple> contents = contentService.getMasterpiecesContents(contentType, pageable);
        return ResponseEntity.ok(new PageResponse<>(contents));
    }

    @GetMapping("/by-keyword")
    public ResponseEntity<Map<String, Object>> getRecommendKeywordContents(
            @RequestParam String contentType,
            @PageableDefault(size = 60, sort = "viewCount", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        Map<String, Object> result = contentService.getRecommendKeywordContents(contentType, pageable);

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
