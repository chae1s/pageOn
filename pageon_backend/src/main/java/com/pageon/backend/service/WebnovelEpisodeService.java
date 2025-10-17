package com.pageon.backend.service;

import com.pageon.backend.dto.response.EpisodeListResponse;
import com.pageon.backend.dto.response.WebnovelEpisodeDetailResponse;
import com.pageon.backend.entity.WebnovelEpisode;
import com.pageon.backend.exception.CustomException;
import com.pageon.backend.exception.ErrorCode;
import com.pageon.backend.repository.WebnovelEpisodeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

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

        Long prevEpisodeId = webnovelEpisodeRepository.findPrevEpisodeId(episode.getWebnovel().getId(), episode.getEpisodeNum());
        Long nextEpisodeId = webnovelEpisodeRepository.findNextEpisodeId(episode.getWebnovel().getId(), episode.getEpisodeNum());

        return WebnovelEpisodeDetailResponse.fromEntity(
                episode.getId(), episode.getWebnovel().getTitle(), episode.getEpisodeNum(), episode.getEpisodeTitle(), episode.getContent(), prevEpisodeId, nextEpisodeId
        );
    }
}
