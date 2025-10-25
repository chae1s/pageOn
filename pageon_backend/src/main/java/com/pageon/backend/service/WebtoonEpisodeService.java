package com.pageon.backend.service;

import com.pageon.backend.dto.response.EpisodeListResponse;
import com.pageon.backend.dto.response.WebtoonEpisodeDetailResponse;
import com.pageon.backend.dto.response.WebtoonImagesResponse;
import com.pageon.backend.entity.WebtoonEpisode;
import com.pageon.backend.exception.CustomException;
import com.pageon.backend.exception.ErrorCode;
import com.pageon.backend.repository.WebtoonEpisodeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class WebtoonEpisodeService {
    private final WebtoonEpisodeRepository webtoonEpisodeRepository;
    private final WebtoonImageService webtoonImageService;

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

    @Transactional(readOnly = true)
    public WebtoonEpisodeDetailResponse getWebtoonEpisodeById(Long id) {
        WebtoonEpisode episode = webtoonEpisodeRepository.findById(id).orElseThrow(
                () -> new CustomException(ErrorCode.EPISODE_NOT_FOUND)
        );

        List<WebtoonImagesResponse> webtoonImages = webtoonImageService.getWebtoonImages(episode.getId());

        Long prevEpisodeId = webtoonEpisodeRepository.findPrevEpisodeId(episode.getWebtoon().getId(), episode.getEpisodeNum());
        Long nextEpisodeId = webtoonEpisodeRepository.findNextEpisodeId(episode.getWebtoon().getId(), episode.getEpisodeNum());

        return WebtoonEpisodeDetailResponse.fromEntity(
                episode, webtoonImages, prevEpisodeId, nextEpisodeId
        );
    }
}
