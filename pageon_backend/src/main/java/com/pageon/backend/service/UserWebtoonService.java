package com.pageon.backend.service;

import com.pageon.backend.common.enums.SerialDay;
import com.pageon.backend.dto.response.*;
import com.pageon.backend.entity.Webnovel;
import com.pageon.backend.entity.Webtoon;
import com.pageon.backend.exception.CustomException;
import com.pageon.backend.exception.ErrorCode;
import com.pageon.backend.repository.WebtoonRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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

    @Transactional(readOnly = true)
    public UserWebtoonResponse getWebtoonById(Long webtoonId) {
        Webtoon webtoon = webtoonRepository.findByIdAndDeleted(webtoonId, false).orElseThrow(
                () -> new CustomException(ErrorCode.WEBTOON_NOT_FOUND)
        );
        List<UserKeywordResponse> keywords = keywordService.getKeywordsExceptCategory(webtoon.getKeywords());
        List<EpisodeListResponse> episodes = webtoonEpisodeService.getEpisodesByWebtoonId(webtoonId);

        return UserWebtoonResponse.fromEntity(webtoon, keywords, episodes);
    }

    @Transactional(readOnly = true)
    public List<UserContentListResponse> getWebtoons() {
        List<Webtoon> webtoons = webtoonRepository.findByDeleted(false);
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
}
