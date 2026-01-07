package com.pageon.backend.service;

import com.pageon.backend.common.enums.ActionType;
import com.pageon.backend.common.enums.ContentType;
import com.pageon.backend.dto.response.EpisodeListResponse;
import com.pageon.backend.dto.response.EpisodePurchaseResponse;
import com.pageon.backend.dto.response.WebnovelEpisodeDetailResponse;
import com.pageon.backend.entity.EpisodePurchase;
import com.pageon.backend.entity.WebnovelEpisode;
import com.pageon.backend.exception.CustomException;
import com.pageon.backend.exception.ErrorCode;
import com.pageon.backend.repository.EpisodePurchaseRepository;
import com.pageon.backend.repository.WebnovelEpisodeRatingRepository;
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
    private final WebnovelEpisodeRatingRepository webnovelEpisodeRatingRepository;
    private final WebnovelEpisodeCommentService webnovelEpisodeCommentService;
    private final EpisodePurchaseRepository episodePurchaseRepository;
    private final ReadingHistoryService readingHistoryService;
    private final ActionLogService actionLogService;

    @Transactional(readOnly = true)
    public List<EpisodeListResponse> getEpisodesByWebnovelId(Long userId, Long webnovelId) {
        List<WebnovelEpisode> webnovelEpisodes = webnovelEpisodeRepository.findByWebnovelId(webnovelId);

        if (userId == null) {
            return webnovelEpisodes.stream()
                    .map(episode -> EpisodeListResponse.fromEntity(episode, null)).toList();
        } else {
            return webnovelEpisodes.stream().map(episode -> {
                EpisodePurchase episodePurchase = episodePurchaseRepository.findByUser_IdAndContentIdAndEpisodeId(userId, webnovelId, episode.getId()).orElse(null);
                return EpisodeListResponse.fromEntity(
                        episode,
                        (episodePurchase != null) ? EpisodePurchaseResponse.fromEntity(episodePurchase) : null
                );
            }).toList();
        }
    }

    @Transactional
    public WebnovelEpisodeDetailResponse getWebnovelEpisodeById(Long userId, Long episodeId) {
        WebnovelEpisode episode = webnovelEpisodeRepository.findByIdWithWebnovel(episodeId).orElseThrow(
                () -> new CustomException(ErrorCode.EPISODE_NOT_FOUND)
        );

        log.info("episode title {}, episode AverageRating {}", episode.getEpisodeTitle(), episode.getAverageRating());

        Long prevEpisodeId = webnovelEpisodeRepository.findPrevEpisodeId(episode.getWebnovel().getId(), episode.getEpisodeNum());
        Long nextEpisodeId = webnovelEpisodeRepository.findNextEpisodeId(episode.getWebnovel().getId(), episode.getEpisodeNum());

        Integer userScore = webnovelEpisodeRatingRepository.findScoreByWebnovelEpisodeAndUser(episode.getId(), userId);

        readingHistoryService.checkReadingHistory(userId, episode.getWebnovel().getId(), episodeId);

        actionLogService.createActionLog(userId, episode.getWebnovel().getId(), ContentType.WEBNOVEL, ActionType.VIEW, 0);

        episode.getWebnovel().updateViewCount();

        return WebnovelEpisodeDetailResponse.fromEntity(
                episode, episode.getWebnovel().getTitle(),
                prevEpisodeId, nextEpisodeId, userScore, webnovelEpisodeCommentService.getBestCommentsByEpisodeId(episodeId)
        );
    }
}
