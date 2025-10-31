package com.pageon.backend.controller;

import com.pageon.backend.common.enums.ContentType;
import com.pageon.backend.dto.request.ContentEpisodeRatingRequest;
import com.pageon.backend.security.PrincipalUser;
import com.pageon.backend.service.RatingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/rating")
@RequiredArgsConstructor
public class RatingController {

    private final RatingService ratingService;

    @PostMapping
    public ResponseEntity<Void> createContentRating(@AuthenticationPrincipal PrincipalUser principalUser, @RequestBody ContentEpisodeRatingRequest request) {
        ContentType contentType = request.getContentType();

        if (contentType == ContentType.WEBNOVEL) {
            ratingService.createWebnovelRating(principalUser.getId(), request);
        } else if (contentType == ContentType.WEBTOON) {
            ratingService.createWebtoonRating(principalUser.getId(), request);
        }

        return ResponseEntity.ok().build();
    }
}
