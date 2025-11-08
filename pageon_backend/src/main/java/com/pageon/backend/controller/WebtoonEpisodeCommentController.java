package com.pageon.backend.controller;

import com.pageon.backend.dto.request.ContentEpisodeCommentRequest;
import com.pageon.backend.dto.response.EpisodeCommentResponse;
import com.pageon.backend.dto.response.PageResponse;
import com.pageon.backend.security.PrincipalUser;
import com.pageon.backend.service.WebtoonEpisodeCommentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Slf4j
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

    @GetMapping
    public ResponseEntity<PageResponse<EpisodeCommentResponse>> getCommentsByEpisodeId(
            @AuthenticationPrincipal PrincipalUser principalUser,
            @PathVariable Long episodeId,
            @PageableDefault(size = 15) Pageable pageable,
            @RequestParam("sort") String sort
    ) {
        log.info("Webtoon episode comments request received. userId = {}, episodeId = {}, sort = {}", principalUser.getId(), episodeId, sort);

        Page<EpisodeCommentResponse> commentResponses = webnovelEpisodeCommentService.getCommentsByEpisodeId(principalUser.getId(), episodeId, pageable, sort);

        return ResponseEntity.ok(new PageResponse<>(commentResponses));
    }

    @PatchMapping("/{commentId}")
    public ResponseEntity<Void> updateComment(
            @AuthenticationPrincipal PrincipalUser principalUser,
            @PathVariable Long episodeId,
            @PathVariable Long commentId,
            @RequestBody ContentEpisodeCommentRequest commentRequest
    ) {
        webnovelEpisodeCommentService.updateComment(principalUser.getId(), commentId, commentRequest);

        return ResponseEntity.ok().build();
    }
}
