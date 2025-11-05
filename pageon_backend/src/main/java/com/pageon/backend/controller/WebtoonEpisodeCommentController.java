package com.pageon.backend.controller;

import com.pageon.backend.dto.request.ContentEpisodeCommentRequest;
import com.pageon.backend.security.PrincipalUser;
import com.pageon.backend.service.WebtoonEpisodeCommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/webtoons/episodes/{episodeId}/comments")
@RequiredArgsConstructor
public class WebtoonEpisodeCommentController {

    private final WebtoonEpisodeCommentService  webnovelEpisodeCommentService;

    @PostMapping()
    public ResponseEntity<Void> createComment(
            @AuthenticationPrincipal PrincipalUser principalUser,
            @PathVariable Long episodeId,
            @RequestBody ContentEpisodeCommentRequest commentRequest) {

        webnovelEpisodeCommentService.createComment(principalUser.getId(), episodeId, commentRequest);

        return ResponseEntity.ok().build();
    }
}
