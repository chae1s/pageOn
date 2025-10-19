package com.pageon.backend.controller;

import com.pageon.backend.common.enums.ContentType;
import com.pageon.backend.dto.response.ContentSimpleResponse;
import com.pageon.backend.dto.response.UserContentListResponse;
import com.pageon.backend.dto.response.UserWebtoonResponse;
import com.pageon.backend.security.PrincipalUser;
import com.pageon.backend.service.LikeService;
import com.pageon.backend.service.UserWebtoonService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/webtoons")
@RequiredArgsConstructor
public class UserWebtoonController {

    private final UserWebtoonService userWebtoonService;
    private final LikeService likeService;

    @GetMapping("/{webtoonId}")
    public ResponseEntity<UserWebtoonResponse> getWebnovelById(@PathVariable Long webtoonId) {

        return ResponseEntity.ok(userWebtoonService.getWebtoonById(webtoonId));
    }

    @GetMapping()
    public ResponseEntity<List<UserContentListResponse>> getWebtoons() {

        return ResponseEntity.ok(userWebtoonService.getWebtoons());
    }

    @GetMapping("/daily/{day}")
    public ResponseEntity<List<ContentSimpleResponse>> getWebtoonsByDay(@PathVariable String day) {

        return ResponseEntity.ok(userWebtoonService.getWebtoonsByDay(day));
    }

    @PostMapping("/{webtoonId}/likes")
    public ResponseEntity<Void> likeWebnovel(
            @AuthenticationPrincipal PrincipalUser principalUser, @PathVariable Long webtoonId
    ) {
        log.info("WEBTOON {} 관심 등록",  webtoonId);
        likeService.registerLike(principalUser.getId(), webtoonId, ContentType.WEBTOON);

        return ResponseEntity.ok().build();
    }


}
