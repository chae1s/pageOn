package com.pageon.backend.controller;

import com.pageon.backend.dto.response.UserContentListResponse;
import com.pageon.backend.dto.response.UserWebnovelResponse;
import com.pageon.backend.service.UserWebnovelService;
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
@RequestMapping("/api/webnovels")
@RequiredArgsConstructor
public class UserWebnovelController {
    private final UserWebnovelService userWebnovelService;

    @GetMapping("/{webnovelId}")
    public ResponseEntity<UserWebnovelResponse> getWebnovelById(@PathVariable Long webnovelId) {

        return ResponseEntity.ok(userWebnovelService.getWebnovelById(webnovelId));
    }

    @GetMapping()
    public ResponseEntity<List<UserContentListResponse>> getWebnovels() {

        return ResponseEntity.ok(userWebnovelService.getWebnovels());
    }
}
