package com.pageon.backend.controller;

import com.pageon.backend.common.enums.ContentType;
import com.pageon.backend.common.enums.PurchaseType;
import com.pageon.backend.security.PrincipalUser;
import com.pageon.backend.service.EpisodePurchaseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class EpisodePurchaseController {
    private final EpisodePurchaseService episodePurchaseService;

    @PostMapping("/webnovels/episodes/{episodeId}/subscribe")
    public ResponseEntity<Void> createWebnovelEpisodeHistory(
            @AuthenticationPrincipal PrincipalUser principalUser,
            @PathVariable Long episodeId,
            @RequestParam PurchaseType purchaseType
    ) {
        episodePurchaseService.createPurchaseHistory(principalUser.getId(), ContentType.WEBNOVEL, episodeId, purchaseType);

        return ResponseEntity.ok().build();
    }

    @PostMapping("/webtoons/episodes/{episodeId}/subscribe")
    public ResponseEntity<Void> createWebtoonEpisodeHistory(
            @AuthenticationPrincipal PrincipalUser principalUser,
            @PathVariable Long episodeId,
            @RequestParam PurchaseType purchaseType
    ) {
        episodePurchaseService.createPurchaseHistory(principalUser.getId(), ContentType.WEBTOON, episodeId, purchaseType);

        return ResponseEntity.ok().build();
    }
}
