package com.pageon.backend.service;

import com.pageon.backend.dto.response.EpisodeListResponse;
import com.pageon.backend.entity.WebtoonEpisode;
import com.pageon.backend.repository.WebtoonEpisodeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class WebtoonEpisodeService {
    private final WebtoonEpisodeRepository webtoonEpisodeRepository;

    public List<EpisodeListResponse> getEpisodesByWebtoonId(Long webtoonId) {
        List<WebtoonEpisode> webtoonEpisodes = webtoonEpisodeRepository.findByWebtoonId(webtoonId);

        return webtoonEpisodes.stream()
                .map(episode -> EpisodeListResponse.fromEntity(
                        episode.getId(),
                        episode.getEpisodeNum(),
                        episode.getEpisodeTitle(),
                        episode.getCreatedAt(),
                        episode.getPurchasePrice(),
                        episode.getRentalPrice()))
                .toList();
    }
}
