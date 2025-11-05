package com.pageon.backend.controller;

import com.pageon.backend.dto.request.ContentEpisodeCommentRequest;
import com.pageon.backend.security.PrincipalUser;
import com.pageon.backend.service.WebnovelEpisodeCommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/webnovels/episodes/{episodeId}/comments")
@RequiredArgsConstructor
public class WebnovelEpisodeCommentController {
    private final WebnovelEpisodeCommentService webnovelEpisodeCommentService;

    @PostMapping()
    public ResponseEntity<Void> createComment(
            @AuthenticationPrincipal PrincipalUser principalUser,
            @PathVariable Long episodeId,
            @RequestBody ContentEpisodeCommentRequest commentRequest){
        webnovelEpisodeCommentService.createComment(principalUser.getId(), episodeId, commentRequest);

        return ResponseEntity.ok().build();
    }


}
