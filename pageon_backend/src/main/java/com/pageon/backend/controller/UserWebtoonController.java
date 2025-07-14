package com.pageon.backend.controller;

import com.pageon.backend.dto.response.UserContentListResponse;
import com.pageon.backend.dto.response.UserWebtoonResponse;
import com.pageon.backend.service.UserWebtoonService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/webtoons")
@RequiredArgsConstructor
public class UserWebtoonController {

    private final UserWebtoonService userWebtoonService;

    @GetMapping("/{webtoonId}")
    public ResponseEntity<UserWebtoonResponse> getWebnovelById(@PathVariable Long webtoonId) {

        return ResponseEntity.ok(userWebtoonService.getWebtoonById(webtoonId));
    }

    @GetMapping()
    public ResponseEntity<List<UserContentListResponse>> getWebtoons() {

        return ResponseEntity.ok(userWebtoonService.getWebtoons());
    }
}
