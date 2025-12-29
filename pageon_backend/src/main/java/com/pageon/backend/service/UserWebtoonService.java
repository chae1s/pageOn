package com.pageon.backend.service;

import com.pageon.backend.common.enums.SerialDay;
import com.pageon.backend.dto.response.*;
import com.pageon.backend.dto.response.UserContentListResponse;
import com.pageon.backend.entity.Content;
import com.pageon.backend.entity.Webtoon;
import com.pageon.backend.exception.CustomException;
import com.pageon.backend.exception.ErrorCode;
import com.pageon.backend.repository.ContentRepository;
import com.pageon.backend.repository.InterestRepository;
import com.pageon.backend.repository.WebtoonRepository;
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
    private final ContentRepository contentRepository;

    @Transactional(readOnly = true)
    public ContentResponse.Detail getWebtoonById(Long webtoonId, PrincipalUser principalUser) {
        Webtoon webtoon = webtoonRepository.findByIdWithDetailInfo(webtoonId).orElseThrow(
                () -> new CustomException(ErrorCode.WEBTOON_NOT_FOUND)
        );

        List<EpisodeListResponse> episodes;

        Boolean isInterested = false;

        if (principalUser != null) {
            Long userId = principalUser.getId();
            episodes = webtoonEpisodeService.getEpisodesByWebtoonId(userId, webtoonId);
            isInterested = interestRepository.existsByUser_IdAndContentId(userId, webtoonId);
        } else {
            episodes = webtoonEpisodeService.getEpisodesByWebtoonId(null, webtoonId);
        }

        return ContentResponse.Detail.fromEntity(webtoon, episodes, isInterested);
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
    public List<ContentResponse.Simple> getWebtoonsByDay(String serialDay) {
        Pageable pageable = PageRequest.of(0, 18);
        List<Webtoon> webtoons = webtoonRepository.findDailyRanking(SerialDay.valueOf(serialDay), pageable);
        log.info("{} 웹툰 검색", serialDay);

        return webtoons.stream()
                .map(ContentResponse.Simple::fromEntity)
                .toList();
    }

    @Transactional(readOnly = true)
    public Page<ContentResponse.Search> getWebtoonsByKeyword(String keywordName, Pageable sortedPageable) {

        Page<Webtoon> webtoonPage = webtoonRepository.findByKeywordName(keywordName, sortedPageable);


        return webtoonPage.map(ContentResponse.Search::fromEntity);
    }

    @Transactional(readOnly = true)
    public Page<ContentResponse.Search> getWebtoonsByTitleOrCreator(String query, Pageable sortedPageable) {


        log.debug("Entering getWebtoonsByTitleOrCreator. Query = [{}], Pageable = {}", query, sortedPageable);
        Page<Webtoon> webtoonPage = webtoonRepository.findByTitleOrPenNameContaining(query, sortedPageable);

        log.debug("Repository found {} webtoons on this page.", webtoonPage.getNumberOfElements());

        log.info("Webtoon search by title/creator successful. Query: [{}]. Found {} total results.",
                query,
                webtoonPage.getTotalElements());

        return webtoonPage.map(ContentResponse.Search::fromEntity);
    }

    @Transactional(readOnly = true)
    public Page<ContentResponse.Simple> getRecentWebtoons(Pageable pageable) {

        LocalDateTime since = LocalDateTime.now().minusDays(180).with(LocalTime.MIN);

        Page<Webtoon> webtoonPage = webtoonRepository.findRecentWebtoons(since, pageable);

        return webtoonPage.map(ContentResponse.Simple::fromEntity);
    }

    @Transactional(readOnly = true)
    public Page<ContentResponse.Simple> getMasterpieceWebtoons(Pageable pageable) {
        Page<Webtoon> webtoons = webtoonRepository.findCompletedMasterpieces(pageable);

        return webtoons.map(ContentResponse.Simple::fromEntity);
    }

}
