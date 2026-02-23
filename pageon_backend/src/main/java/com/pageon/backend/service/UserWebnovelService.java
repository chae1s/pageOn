package com.pageon.backend.service;

import com.pageon.backend.dto.response.*;
import com.pageon.backend.entity.Keyword;
import com.pageon.backend.entity.Webnovel;
import com.pageon.backend.exception.CustomException;
import com.pageon.backend.exception.ErrorCode;
import com.pageon.backend.repository.InterestRepository;
import com.pageon.backend.repository.KeywordRepository;
import com.pageon.backend.repository.WebnovelRepository;
import com.pageon.backend.security.PrincipalUser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserWebnovelService {

    private final WebnovelRepository webnovelRepository;
    private final WebnovelEpisodeService  webnovelEpisodeService;
    private final InterestRepository interestRepository;
    private final KeywordRepository keywordRepository;

    @Transactional(readOnly = true)
    public ContentResponse.Detail getWebnovelById(Long webnovelId, PrincipalUser principalUser) {

        log.info("Getting Webnovel by id {}", webnovelId);
        Webnovel webnovel = webnovelRepository.findByIdWithDetailInfo(webnovelId).orElseThrow(
                () -> new CustomException(ErrorCode.WEBNOVEL_NOT_FOUND)
        );

        Boolean isInterested = false;

        List<EpisodeListResponse> episodes;

        if (principalUser != null) {
            Long userId = principalUser.getId();
            episodes = webnovelEpisodeService.getEpisodesByWebnovelId(principalUser.getId(), webnovelId);
            isInterested = interestRepository.existsByUser_IdAndContentId(userId, webnovelId);
        } else {
            episodes = webnovelEpisodeService.getEpisodesByWebnovelId(null, webnovelId);
        }

        log.info("IsInterested: {}", isInterested);

        ContentResponse.Detail detail = ContentResponse.Detail.fromEntity(webnovel, episodes, isInterested);

        log.info("detail: {}", webnovel.getContentKeywords());

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
    public Page<ContentResponse.Search> getWebnovelsByKeyword(String keywordName, Pageable sortedPageable) {

        Page<Webnovel> webnovelPage = webnovelRepository.findByKeywordName(keywordName, sortedPageable);

        return webnovelPage.map(ContentResponse.Search::fromEntity);
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


    @Transactional(readOnly = true)
    public Map<String, Object> getRecommendKeywordWebnovels(Pageable pageable) {
        LocalDate currentDate = LocalDate.now();

        Keyword keyword = keywordRepository.findValidKeyword(currentDate).orElseThrow(
                () -> new CustomException(ErrorCode.INVALID_KEYWORD)
        );

        Page<Webnovel> webnovels = webnovelRepository.findByKeywordName(keyword.getName(), pageable);
        Page<ContentResponse.Simple> responses = webnovels.map(ContentResponse.Simple::fromEntity);

        Map<String, Object> result = new HashMap<>();
        result.put("keyword", keyword.getName());
        result.put("contents", new PageResponse<>(responses));

        return result;
    }

}
