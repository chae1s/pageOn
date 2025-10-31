package com.pageon.backend.service;

import com.pageon.backend.dto.response.EpisodeListResponse;
import com.pageon.backend.dto.response.WebnovelEpisodeDetailResponse;
import com.pageon.backend.entity.WebnovelEpisode;
import com.pageon.backend.exception.CustomException;
import com.pageon.backend.exception.ErrorCode;
import com.pageon.backend.repository.WebnovelEpisodeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class WebnovelEpisodeService {

    private final WebnovelEpisodeRepository webnovelEpisodeRepository;

    @Transactional(readOnly = true)
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

    @Transactional(readOnly = true)
    public WebnovelEpisodeDetailResponse getWebnovelEpisodeById(Long id) {
        WebnovelEpisode episode = webnovelEpisodeRepository.findById(id).orElseThrow(
                () -> new CustomException(ErrorCode.EPISODE_NOT_FOUND)
        );

        log.info("episode title {}, episode AverageRating {}", episode.getEpisodeTitle(), episode.getAverageRating());

        Long prevEpisodeId = webnovelEpisodeRepository.findPrevEpisodeId(episode.getWebnovel().getId(), episode.getEpisodeNum());
        Long nextEpisodeId = webnovelEpisodeRepository.findNextEpisodeId(episode.getWebnovel().getId(), episode.getEpisodeNum());

        return WebnovelEpisodeDetailResponse.fromEntity(
                episode, episode.getWebnovel().getTitle(), prevEpisodeId, nextEpisodeId
        );
    }
}
