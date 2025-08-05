package com.pageon.backend.service;

import com.pageon.backend.dto.response.EpisodeListResponse;
import com.pageon.backend.entity.WebnovelEpisode;
import com.pageon.backend.repository.WebnovelEpisodeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class WebnovelEpisodeService {

    private final WebnovelEpisodeRepository webnovelEpisodeRepository;

    public List<EpisodeListResponse> getEpisodesByWebnovelId(Long webnovelId) {
        List<WebnovelEpisode> webnovelEpisodes = webnovelEpisodeRepository.findByWebnovelId(webnovelId);

        return webnovelEpisodes.stream()
                .map(episode -> EpisodeListResponse.fromEntity(
                        episode.getId(),
                        episode.getEpisodeNum(),
                        episode.getEpisodeTitle(),
                        episode.getCreatedAt(),
                        episode.getPurchasePrice(),
                        null))
                .toList();
    }
}
