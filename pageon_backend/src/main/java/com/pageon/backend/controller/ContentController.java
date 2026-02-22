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

import java.time.LocalDate;
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
    public ResponseEntity<?> getRecentContents(
            @RequestParam String contentType,
            @PageableDefault(size = 60, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable,
            @RequestParam(defaultValue = "false") boolean isMore
    ) {

        if (isMore) {
            Page<ContentResponse.Simple> contents = contentService.getRecentContents(contentType, pageable);

            return ResponseEntity.ok(new PageResponse<>(contents));
        }

        List<ContentResponse.Simple> contents = contentService.getRecentContents(contentType, LocalDate.now());

        return ResponseEntity.ok(contents);
    }

    @GetMapping("/masterpiece")
    public ResponseEntity<?> getMasterpiecesContents(
            @RequestParam("contentType") String contentType,
            @PageableDefault(size = 60, sort = "viewCount", direction = Sort.Direction.DESC) Pageable pageable,
            @RequestParam(defaultValue = "false") boolean isMore
    ) {

        if (isMore) {
            Page<ContentResponse.Simple> contents = contentService.getMasterpiecesContents(contentType, pageable);

            return ResponseEntity.ok(new PageResponse<>(contents));
        }

        List<ContentResponse.Simple> contents = contentService.getMasterpiecesContents(contentType);
        return ResponseEntity.ok(contents);
    }

    @GetMapping("/by-keyword")
    public ResponseEntity<Map<String, Object>> getRecommendKeywordContents(
            @RequestParam String contentType,
            @PageableDefault(size = 60, sort = "viewCount", direction = Sort.Direction.DESC) Pageable pageable,
            @RequestParam(defaultValue = "false") boolean isMore
    ) {
        Map<String, Object> contents;

        if (isMore) {
            contents = contentService.getRecommendKeywordContents(contentType, pageable);
        } else {
            contents = contentService.getRecommendKeywordContents(contentType);
        }

        return ResponseEntity.ok(contents);
    }

    @GetMapping("/hourly-ranking")
    public ResponseEntity<List<ContentResponse.Simple>> getHourlyRankingContents(
            @RequestParam String contentType
    ) {

        List<ContentResponse.Simple> contents = rankingService.getHourlyRankingContents(contentType);

        return ResponseEntity.ok(contents);
    }
}
