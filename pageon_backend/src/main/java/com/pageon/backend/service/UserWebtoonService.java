package com.pageon.backend.service;

import com.pageon.backend.common.enums.ContentType;
import com.pageon.backend.common.enums.SerialDay;
import com.pageon.backend.common.utils.PageableUtil;
import com.pageon.backend.dto.response.*;
import com.pageon.backend.dto.response.ContentSimpleResponse;
import com.pageon.backend.dto.response.UserContentListResponse;
import com.pageon.backend.dto.response.UserWebtoonResponse;
import com.pageon.backend.entity.Webnovel;
import com.pageon.backend.entity.Webtoon;
import com.pageon.backend.exception.CustomException;
import com.pageon.backend.exception.ErrorCode;
import com.pageon.backend.repository.InterestRepository;
import com.pageon.backend.repository.WebtoonRepository;
import com.pageon.backend.security.PrincipalUser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserWebtoonService {

    private final WebtoonRepository webtoonRepository;
    private final KeywordService keywordService;
    private final WebtoonEpisodeService webtoonEpisodeService;
    private final InterestRepository interestRepository;

    @Transactional(readOnly = true)
    public UserWebtoonResponse getWebtoonById(Long webtoonId, PrincipalUser principalUser) {
        Webtoon webtoon = webtoonRepository.findByIdAndDeletedAtIsNull(webtoonId).orElseThrow(
                () -> new CustomException(ErrorCode.WEBTOON_NOT_FOUND)
        );
        List<UserKeywordResponse> keywords = keywordService.getKeywordsExceptCategory(webtoon.getKeywords());
        List<EpisodeListResponse> episodes;

        Boolean isInterested = false;

        if (principalUser != null) {
            Long userId = principalUser.getId();
            episodes = webtoonEpisodeService.getEpisodesByWebtoonId(userId, webtoonId);
            isInterested = interestRepository.existsByUser_IdAndContentTypeAndContentId(userId, ContentType.WEBTOON, webtoonId);
        } else {
            episodes = webtoonEpisodeService.getEpisodesByWebtoonId(webtoonId);
        }

        return UserWebtoonResponse.fromEntity(webtoon, keywords, episodes, isInterested);
    }

    @Transactional(readOnly = true)
    public List<UserContentListResponse> getWebtoons() {
        List<Webtoon> webtoons = webtoonRepository.findByDeletedAtIsNull();
        List<UserContentListResponse> webnovelListResponses = new ArrayList<>();

        for (Webtoon webtoon : webtoons) {
            List<UserKeywordResponse> keywords = keywordService.getKeywordsExceptCategory(webtoon.getKeywords());
            webnovelListResponses.add(UserContentListResponse.fromWebtoon(webtoon, keywords, 0, 0));
        }

        return webnovelListResponses;
    }

    @Transactional(readOnly = true)
    public List<ContentSimpleResponse> getWebtoonsByDay(String serialDay) {
        Pageable pageable = PageRequest.of(0, 18);
        List<Webtoon> webtoons = webtoonRepository.findDailyRanking(SerialDay.valueOf(serialDay), pageable);
        log.info("{} 웹툰 검색", serialDay);

        return webtoons.stream()
                .map(w -> ContentSimpleResponse.fromEntity(
                        w.getId(),
                        w.getTitle(),
                        w.getCreator().getPenName(),
                        w.getCover(),
                        "webtoons"))
                .toList();
    }

    @Transactional(readOnly = true)
    public Page<ContentSearchResponse> getWebtoonsByKeyword(String keywordName, String sort, Pageable pageable) {

        Pageable sortedPageable = PageableUtil.createContentPageable(pageable, sort);

        Page<Webtoon> webtoonPage = webtoonRepository.findByKeywordName(keywordName, sortedPageable);


        return webtoonPage.map(webtoon ->
                ContentSearchResponse.fromEntity(webtoon, "webtoons")
        );
    }

    @Transactional(readOnly = true)
    public Page<ContentSearchResponse> getWebtoonsByTitleOrCreator(String query, Pageable sortedPageable) {


        log.debug("Entering getWebtoonsByTitleOrCreator. Query = [{}], Pageable = {}", query, sortedPageable);
        Page<Webtoon> webtoonPage = webtoonRepository.findByTitleOrPenNameContaining(query, sortedPageable);

        log.debug("Repository found {} webtoons on this page.", webtoonPage.getNumberOfElements());

        log.info("Webtoon search by title/creator successful. Query: [{}]. Found {} total results.",
                query,
                webtoonPage.getTotalElements());

        return webtoonPage.map(webtoon ->
                ContentSearchResponse.fromEntity(webtoon, "webtoons")
        );
    }

}
