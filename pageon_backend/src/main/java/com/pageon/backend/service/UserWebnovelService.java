package com.pageon.backend.service;

import com.pageon.backend.common.enums.ContentType;
import com.pageon.backend.common.enums.SerialDay;
import com.pageon.backend.dto.response.*;
import com.pageon.backend.dto.response.ContentSimpleResponse;
import com.pageon.backend.dto.response.UserContentListResponse;
import com.pageon.backend.dto.response.UserWebnovelResponse;
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

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserWebnovelService {

    private final WebnovelRepository webnovelRepository;
    private final KeywordService keywordService;
    private final WebnovelEpisodeService  webnovelEpisodeService;
    private final InterestRepository interestRepository;

    @Transactional(readOnly = true)
    public UserWebnovelResponse getWebnovelById(Long webnovelId, PrincipalUser principalUser) {

        Webnovel webnovel = webnovelRepository.findByIdAndDeleted(webnovelId, false).orElseThrow(
                () -> new CustomException(ErrorCode.WEBNOVEL_NOT_FOUND)
        );

        List<UserKeywordResponse> keywords = keywordService.getKeywordsExceptCategory(webnovel.getKeywords());
        List<EpisodeListResponse> episodes = webnovelEpisodeService.getEpisodesByWebnovelId(webnovelId);

        Boolean isInterested = false;

        if (principalUser != null) {
            Long userId = principalUser.getId();
            log.info("UserWebnovelService.getWebnovelById: userId = " + userId);
            isInterested = interestRepository.existsByUser_IdAndContentTypeAndContentId(userId, ContentType.WEBNOVEL, webnovelId);
        }

        log.info("IsInterested: {}", isInterested);

        return UserWebnovelResponse.fromEntity(webnovel, keywords, episodes, isInterested);
    }

    @Transactional(readOnly = true)
    public List<UserContentListResponse> getWebnovels() {
        List<Webnovel> webnovels = webnovelRepository.findByDeleted(false);
        List<UserContentListResponse> webnovelListResponses = new ArrayList<>();

        for (Webnovel webnovel : webnovels) {
            List<UserKeywordResponse> keywords = keywordService.getKeywordsExceptCategory(webnovel.getKeywords());
            webnovelListResponses.add(UserContentListResponse.fromWebnovel(webnovel, keywords, 0, 0));
        }

        return webnovelListResponses;
    }

    @Transactional(readOnly = true)
    public List<ContentSimpleResponse> getWebnovelsByDay(String serialDay) {
        Pageable pageable = PageRequest.of(0, 18);
        List<Webnovel> webnovels = webnovelRepository.findDailyRanking(SerialDay.valueOf(serialDay), pageable);
        log.info("{} 웹소설 검색", serialDay);

        return webnovels.stream()
                .map(w -> ContentSimpleResponse.fromEntity(
                        w.getId(),
                        w.getTitle(),
                        w.getCreator().getPenName(),
                        w.getCover(),
                        "webnovels"))
                .toList();
    }

    @Transactional(readOnly = true)
    public Page<ContentSearchResponse> getWebnovelsByKeyword(String keywordName, Pageable pageable) {
        Page<Webnovel> webnovelPage = webnovelRepository.findByKeywordName(keywordName, pageable);

        return webnovelPage.map(ContentSearchResponse::fromWebnovel);
    }


}
