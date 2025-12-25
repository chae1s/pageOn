package com.pageon.backend.controller;

import com.pageon.backend.dto.response.PageResponse;
import com.pageon.backend.dto.response.ContentSearchResponse;
import com.pageon.backend.exception.CustomException;
import com.pageon.backend.exception.ErrorCode;
import com.pageon.backend.service.SearchService;
import com.pageon.backend.service.UserWebnovelService;
import com.pageon.backend.service.UserWebtoonService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/search")
@RequiredArgsConstructor
public class SearchController {

    private final UserWebnovelService userWebnovelService;
    private final UserWebtoonService userWebtoonService;
    private final SearchService searchService;

    @GetMapping("/keywords")
    public ResponseEntity<PageResponse<ContentSearchResponse>> getContentsByKeyword(
            @RequestParam("type") String contentType, @RequestParam("q") String query, @RequestParam("sort") String sort, @PageableDefault(size = 20) Pageable pageable) {

        if (contentType.equals("webnovels")) {
            Page<ContentSearchResponse> contentSearchResponses = userWebnovelService.getWebnovelsByKeyword(query, sort, pageable);
            return ResponseEntity.ok(new PageResponse<>(contentSearchResponses));
        } else if (contentType.equals("webtoons")) {
            Page<ContentSearchResponse> contentSearchResponses = userWebtoonService.getWebtoonsByKeyword(query, sort, pageable);

            return ResponseEntity.ok(new PageResponse<>(contentSearchResponses));
        }

        return ResponseEntity.notFound().build();
    }

    @GetMapping
    public ResponseEntity<PageResponse<ContentSearchResponse>> getContentsByTitleOrCreator(
            @RequestParam("type") String contentType, @RequestParam("q") String query, @RequestParam("sort") String sort, @PageableDefault(size = 20) Pageable pageable
    ) {
        Page<ContentSearchResponse> contentSearchResponses = searchService.getContentsByTitleOrCreator(contentType, query, sort, pageable);

        return ResponseEntity.ok(new PageResponse<>(contentSearchResponses));
    }
}
