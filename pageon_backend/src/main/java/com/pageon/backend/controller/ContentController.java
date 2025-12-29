package com.pageon.backend.controller;

import com.pageon.backend.dto.response.ContentResponse;
import com.pageon.backend.dto.response.PageResponse;
import com.pageon.backend.service.ContentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/contents")
@RequiredArgsConstructor
public class ContentController {
    private final ContentService contentService;

    @GetMapping("/masterpiece")
    public ResponseEntity<PageResponse<ContentResponse.Simple>> getMasterpieceContents(@PageableDefault(size = 60, sort = "viewCount", direction = Sort.Direction.DESC) Pageable pageable) {

        Page<ContentResponse.Simple> contents = contentService.getMasterpieceContents(pageable);

        return ResponseEntity.ok(new PageResponse<>(contents));
    }

}
