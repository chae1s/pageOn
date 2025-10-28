package com.pageon.backend.controller;

import com.pageon.backend.dto.response.ContentPageResponse;
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

import java.util.ArrayList;
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
    public ResponseEntity<ContentPageResponse<ContentSearchResponse>> getContentsByKeyword(
            @RequestParam("type") String contentType, @RequestParam("q") String query, @PageableDefault(size = 20) Pageable pageable) {

        if (contentType.equals("webnovels")) {
            Page<ContentSearchResponse> contentSearchResponses = userWebnovelService.getWebnovelsByKeyword(query, pageable);
            return ResponseEntity.ok(new ContentPageResponse<>(contentSearchResponses));
        } else if (contentType.equals("webtoons")) {
            Page<ContentSearchResponse> contentSearchResponses = userWebtoonService.getWebtoonsByKeyword(query, pageable);

            return ResponseEntity.ok(new ContentPageResponse<>(contentSearchResponses));
        }

        return ResponseEntity.notFound().build();
    }

    @GetMapping
    public ResponseEntity<ContentPageResponse<ContentSearchResponse>> getContentsByTitleOrCreator(
            @RequestParam("type") String contentType, @RequestParam("q") String query, @PageableDefault(size = 20) Pageable pageable
    ) {

        log.info("Search request received. Type: [{}], Query[{}], Page: {}, size: {}",
                contentType, query, pageable.getPageNumber(), pageable.getPageSize());

        List<String> contentTypes = List.of("webnovels", "webtoons", "all");
        if (!contentTypes.contains(contentType)) {
            throw new CustomException(ErrorCode.INVALID_CONTENT_TYPE);
        }

        switch (contentType) {
            case "webnovels" -> {
                log.debug("Routing to 'webnovel' service");
                Page<ContentSearchResponse> contentSearchResponses = userWebnovelService.getWebnovelsByTitleOrCreator(query, pageable);

                return ResponseEntity.ok(new ContentPageResponse<>(contentSearchResponses));
            }
            case "webtoons" -> {
                log.debug("Routing to 'webtoon' service");
                Page<ContentSearchResponse> contentSearchResponses = userWebtoonService.getWebtoonsByTitleOrCreator(query, pageable);

                return ResponseEntity.ok(new ContentPageResponse<>(contentSearchResponses));
            }
            case "all" -> {
                log.debug("Routing to 'all' service");
                Page<ContentSearchResponse> contentSearchResponses = searchService.getContentsByTitleOrCreator(query, pageable);

                return ResponseEntity.ok(new ContentPageResponse<>(contentSearchResponses));
            }
        }

        return ResponseEntity.notFound().build();
    }
}
