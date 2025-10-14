package com.pageon.backend.controller;

import com.pageon.backend.dto.response.WebnovelEpisodeDetailResponse;
import com.pageon.backend.service.WebnovelEpisodeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/episodes/")
public class EpisodeController {
    private final WebnovelEpisodeService webnovelEpisodeService;

    @GetMapping("/webnovel/{episodeId}")
    public WebnovelEpisodeDetailResponse getWebnovelEpisodeById(@PathVariable Long episodeId) {
        log.info("WebnovelEpisodeController getWebnovelEpisodeById");
        return webnovelEpisodeService.getWebnovelEpisodeById(episodeId);
    }
}
