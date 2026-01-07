package com.pageon.backend.service;

import com.pageon.backend.common.enums.ActionType;
import com.pageon.backend.common.enums.ContentType;
import com.pageon.backend.dto.request.ContentEpisodeRatingRequest;
import com.pageon.backend.entity.*;
import com.pageon.backend.exception.CustomException;
import com.pageon.backend.exception.ErrorCode;
import com.pageon.backend.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class RatingService {

    private final UserRepository userRepository;
    private final WebtoonEpisodeRepository webtoonEpisodeRepository;
    private final WebnovelEpisodeRepository webnovelEpisodeRepository;
    private final WebtoonEpisodeRatingRepository webtoonEpisodeRatingRepository;
    private final WebnovelEpisodeRatingRepository webnovelEpisodeRatingRepository;
    private final ActionLogService actionLogService;

    @Transactional
    public void createWebtoonRating(Long userId, ContentEpisodeRatingRequest contentEpisodeRatingRequest) {
        final Long episodeId = contentEpisodeRatingRequest.getEpisodeId();
        final Integer score = contentEpisodeRatingRequest.getScore();

        log.info("[START] createWebtoonRating: userId={}, episodeId={}", userId, episodeId);

        User user = userRepository.getReferenceById(userId);

        WebtoonEpisode webtoonEpisode = webtoonEpisodeRepository.findByIdWithWebtoon(episodeId).orElseThrow(
                () -> {
                    log.error("Failed to find WebtoonEpisode: episodeId = {}", episodeId);
                    return new CustomException(ErrorCode.EPISODE_NOT_FOUND);
                }
        );

        WebtoonEpisodeRating webtoonEpisodeRating = WebtoonEpisodeRating.builder()
                .user(user)
                .webtoonEpisode(webtoonEpisode)
                .score(score)
                .build();

        webtoonEpisodeRatingRepository.save(webtoonEpisodeRating);
        log.info("New WebtoonEpisodeRating saved: newRatingId = {}, userId = {}, episodeId = {}",
                webtoonEpisodeRating.getId(), userId, episodeId);

        log.info("Updating aggregates for WebtoonEpisode: episodeId = {}", episodeId);
        webtoonEpisode.addRating(score);

        Webtoon webtoon = webtoonEpisode.getWebtoon();
        log.info("Updating aggregates for Webtoon: webtoonId = {}", webtoon.getId());
        webtoon.addRating(score);

        actionLogService.createActionLog(userId, webtoon.getId(), ContentType.WEBTOON, ActionType.RATING, score);
        log.info("[SUCCESS] createWebtoonRating committed: userId={}, episodeId={}", userId, episodeId);
    }

    @Transactional
    public void createWebnovelRating(Long userId, ContentEpisodeRatingRequest contentEpisodeRatingRequest) {
        final Long episodeId = contentEpisodeRatingRequest.getEpisodeId();
        final Integer score = contentEpisodeRatingRequest.getScore();

        log.info("[START] createWebnovelRating: userId={}, episodeId={}", userId, episodeId);

        User user = userRepository.getReferenceById(userId);

        WebnovelEpisode webnovelEpisode = webnovelEpisodeRepository.findByIdWithWebnovel(episodeId).orElseThrow(
                () -> {
                    log.error("Failed to find WebnovelEpisode: episodeId = {}", episodeId);
                    return new CustomException(ErrorCode.EPISODE_NOT_FOUND);
                }
        );

        WebnovelEpisodeRating webnovelEpisodeRating = WebnovelEpisodeRating.builder()
                .user(user)
                .webnovelEpisode(webnovelEpisode)
                .score(score)
                .build();

        webnovelEpisodeRatingRepository.save(webnovelEpisodeRating);
        log.info("New WebnovelEpisodeRating saved: newRatingId = {}, userId = {}, episodeId = {}",
                webnovelEpisodeRating.getId(), userId, episodeId);

        log.info("Updating aggregates for WebnovelEpisode: episodeId = {}", episodeId);
        webnovelEpisode.addRating(score);

        Webnovel webnovel = webnovelEpisode.getWebnovel();
        log.info("Updating aggregates for Webnovel: webnovelId = {}", webnovel.getId());
        webnovel.addRating(score);

        actionLogService.createActionLog(userId, webnovel.getId(), ContentType.WEBNOVEL, ActionType.RATING, score);

        log.info("[SUCCESS] createWebnovelRating committed: userId={}, episodeId={}", userId, episodeId);
    }

    @Transactional
    public void updateWebnovelRating(Long userId, ContentEpisodeRatingRequest contentEpisodeRatingRequest) {
        final Long episodeId = contentEpisodeRatingRequest.getEpisodeId();
        final Integer newScore = contentEpisodeRatingRequest.getScore();

        log.info("[START] updateWebnovelRating: userId={}, episodeId={}", userId, episodeId);
        WebnovelEpisode episode = webnovelEpisodeRepository.findByIdWithWebnovel(episodeId).orElseThrow(
                () -> {
                    log.error("Failed to find WebnovelEpisode: episodeId = {}", episodeId);
                    return new CustomException(ErrorCode.EPISODE_NOT_FOUND);
                }
        );

        WebnovelEpisodeRating webnovelEpisodeRating = webnovelEpisodeRatingRepository.findByWebnovelEpisodeAndUser(episodeId, userId).orElseThrow(
                () -> new CustomException(ErrorCode.EPISODE_RATING_NOT_FOUND)
        );

        Integer oldScore = webnovelEpisodeRating.getScore();

        log.info("Found webnovelEpisodeRating old score = {}", oldScore);

        log.info("Updating for WebnovelEpisodeRating: new score = {}", newScore);
        webnovelEpisodeRating.updateRating(newScore);

        Webnovel webnovel = episode.getWebnovel();
        log.info("Updating aggregates for WebnovelEpisode and Webnovel: webnovelEpisodeId = {}, webnovelId = {}", episodeId, webnovel.getId());
        episode.updateRating(oldScore, newScore);
        webnovel.updateRating(oldScore, newScore);
    }


    @Transactional
    public void updateWebtoonRating(Long userId, ContentEpisodeRatingRequest contentEpisodeRatingRequest) {
        final Long episodeId = contentEpisodeRatingRequest.getEpisodeId();
        final Integer newScore = contentEpisodeRatingRequest.getScore();

        log.info("[START] updateWebtoonRating: userId={}, episodeId={}", userId, episodeId);
        WebtoonEpisode episode = webtoonEpisodeRepository.findByIdWithWebtoon(episodeId).orElseThrow(
                () -> {
                    log.error("Failed to find WebtoonEpisode: episodeId = {}", episodeId);
                    return new CustomException(ErrorCode.EPISODE_NOT_FOUND);
                }
        );

        WebtoonEpisodeRating webtoonEpisodeRating = webtoonEpisodeRatingRepository.findByWebtoonEpisodeAndUser(episodeId, userId).orElseThrow(
                () -> new CustomException(ErrorCode.EPISODE_RATING_NOT_FOUND)
        );

        Integer oldScore = webtoonEpisodeRating.getScore();
        log.info("Found webtoonEpisodeRating old score = {}", oldScore);


        log.info("Updating for WebtoonEpisodeRating: new score = {}", newScore);
        webtoonEpisodeRating.updateRating(newScore);

        Webtoon webtoon = episode.getWebtoon();

        log.info("Updating aggregates for WebtoonEpisode and Webtoon: webtoonEpisodeId = {}, webtoonId = {}", episodeId, webtoon.getId());
        episode.updateRating(oldScore, newScore);
        webtoon.updateRating(oldScore, newScore);
    }
}
