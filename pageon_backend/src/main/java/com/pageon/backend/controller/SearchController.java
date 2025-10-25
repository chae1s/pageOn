package com.pageon.backend.controller;

import com.pageon.backend.dto.response.ContentPageResponse;
import com.pageon.backend.dto.response.ContentSearchResponse;
import com.pageon.backend.exception.CustomException;
import com.pageon.backend.exception.ErrorCode;
import com.pageon.backend.service.UserWebnovelService;
import com.pageon.backend.service.UserWebtoonService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/search")
@RequiredArgsConstructor
public class SearchController {

    private final UserWebnovelService userWebnovelService;
    private final UserWebtoonService userWebtoonService;

    @GetMapping("/keywords")
    public ResponseEntity<ContentPageResponse<ContentSearchResponse>> getWebnovelsByKeyword(
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
}
