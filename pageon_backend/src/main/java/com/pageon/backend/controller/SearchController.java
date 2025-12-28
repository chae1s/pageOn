package com.pageon.backend.controller;

import com.pageon.backend.dto.response.ContentResponse;
import com.pageon.backend.dto.response.PageResponse;
import com.pageon.backend.service.SearchService;
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


@Slf4j
@RestController
@RequestMapping("/api/search")
@RequiredArgsConstructor
public class SearchController {

    private final SearchService searchService;

    @GetMapping("/keywords")
    public ResponseEntity<PageResponse<ContentResponse.Search>> getContentsByKeyword(
            @RequestParam("type") String contentType, @RequestParam("q") String query, @RequestParam("sort") String sort, @PageableDefault(size = 20) Pageable pageable) {

        Page<ContentResponse.Search> contents = searchService.getContentsByKeyword(contentType, query, sort, pageable);

        return ResponseEntity.ok(new PageResponse<>(contents));
    }

    @GetMapping
    public ResponseEntity<PageResponse<ContentResponse.Search>> getContentsByTitleOrCreator(
            @RequestParam("type") String contentType, @RequestParam("q") String query, @RequestParam("sort") String sort, @PageableDefault(size = 20) Pageable pageable
    ) {
        Page<ContentResponse.Search> contents = searchService.getContentsByTitleOrCreator(contentType, query, sort, pageable);

        return ResponseEntity.ok(new PageResponse<>(contents));
    }
}
