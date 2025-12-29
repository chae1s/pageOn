package com.pageon.backend.service;

import com.pageon.backend.common.enums.SerialDay;
import com.pageon.backend.dto.response.*;
import com.pageon.backend.entity.Webnovel;
import com.pageon.backend.exception.CustomException;
import com.pageon.backend.exception.ErrorCode;
import com.pageon.backend.repository.InterestRepository;
import com.pageon.backend.repository.WebnovelRepository;
import com.pageon.backend.security.PrincipalUser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserWebnovelService {

    private final WebnovelRepository webnovelRepository;
    private final WebnovelEpisodeService  webnovelEpisodeService;
    private final InterestRepository interestRepository;

    @Transactional(readOnly = true)
    public ContentResponse.Detail getWebnovelById(Long webnovelId, PrincipalUser principalUser) {

        Webnovel webnovel = webnovelRepository.findByIdWithDetailInfo(webnovelId).orElseThrow(
                () -> new CustomException(ErrorCode.WEBNOVEL_NOT_FOUND)
        );

        Boolean isInterested = false;

        List<EpisodeListResponse> episodes;

        if (principalUser != null) {
            Long userId = principalUser.getId();
            log.info("UserWebnovelService.getWebnovelById: userId = " + userId);
            episodes = webnovelEpisodeService.getEpisodesByWebnovelId(principalUser.getId(), webnovelId);
            isInterested = interestRepository.existsByUser_IdAndContentId(userId, webnovelId);
        } else {
            episodes = webnovelEpisodeService.getEpisodesByWebnovelId(null, webnovelId);
        }

        log.info("IsInterested: {}", isInterested);

        return ContentResponse.Detail.fromEntity(webnovel, episodes, isInterested);
    }

    @Transactional(readOnly = true)
    public List<ContentResponse.Summary> getWebnovels() {
        List<Webnovel> webnovels = webnovelRepository.findByDeletedAtIsNull();


        return webnovels.stream()
                .map(ContentResponse.Summary::fromEntity)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ContentResponse.Simple> getWebnovelsByDay(String serialDay) {
        Pageable pageable = PageRequest.of(0, 18);
        List<Webnovel> webnovels = webnovelRepository.findDailyRanking(SerialDay.valueOf(serialDay), pageable);
        log.info("{} 웹소설 검색", serialDay);

        return webnovels.stream()
                .map(ContentResponse.Simple::fromEntity)
                .toList();
    }

    @Transactional(readOnly = true)
    public Page<ContentResponse.Search> getWebnovelsByKeyword(String keywordName, Pageable sortedPageable) {

        Page<Webnovel> webnovelPage = webnovelRepository.findByKeywordName(keywordName, sortedPageable);

        return webnovelPage.map(ContentResponse.Search::fromEntity
        );
    }

    @Transactional(readOnly = true)
    public Page<ContentResponse.Search> getWebnovelsByTitleOrCreator(String query, Pageable sortedPageable) {

        log.debug("Entering getWebnovelsByTitleOrCreator. Query = [{}], Pageable = {}", query, sortedPageable);
        Page<Webnovel> webnovelPage = webnovelRepository.findByTitleOrPenNameContaining(query, sortedPageable);

        log.debug("Repository found {} webnovels on this page.", webnovelPage.getNumberOfElements());

        log.info("Webnovel search by title/creator successful. Query: [{}]. Found {} total results.",
                query,
                webnovelPage.getTotalElements());

        return webnovelPage.map(ContentResponse.Search::fromEntity);
    }

    @Transactional(readOnly = true)
    public Page<ContentResponse.Simple> getRecentWebnovels(Pageable pageable) {

        LocalDateTime since = LocalDateTime.now().minusDays(180).with(LocalTime.MIN);

        Page<Webnovel> webnovelPage = webnovelRepository.findRecentWebnovels(since, pageable);

        return webnovelPage.map(ContentResponse.Simple::fromEntity);
    }

}
