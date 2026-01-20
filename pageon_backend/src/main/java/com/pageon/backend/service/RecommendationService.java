package com.pageon.backend.service;

import com.pageon.backend.common.annotation.ExecutionTimer;
import com.pageon.backend.common.utils.PageableUtil;
import com.pageon.backend.dto.response.ContentResponse;
import com.pageon.backend.dto.response.PageResponse;
import com.pageon.backend.entity.*;
import com.pageon.backend.exception.CustomException;
import com.pageon.backend.exception.ErrorCode;
import com.pageon.backend.repository.ContentRepository;
import com.pageon.backend.repository.KeywordRepository;
import com.pageon.backend.repository.WebnovelRepository;
import com.pageon.backend.repository.WebtoonRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StopWatch;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class RecommendationService {
    private final ContentRepository contentRepository;
    private final WebnovelRepository webnovelRepository;
    private final WebtoonRepository webtoonRepository;
    private final KeywordRepository keywordRepository;

    @ExecutionTimer
    @Transactional(readOnly = true)
    public Page<ContentResponse.Simple> getMasterpiecesContents(String contentType, Pageable pageable) {

        Page<ContentResponse.Simple> contents;
        switch (contentType) {
            case "all" -> {
                Page<Content> contentPage = contentRepository.findCompletedMasterpieces(pageable);
                contents = contentPage.map(ContentResponse.Simple::fromEntity);
            }
            case "webnovels" -> {
                Page<Webnovel> webnovelPage = webnovelRepository.findCompletedMasterpieces(pageable);
                contents = webnovelPage.map(ContentResponse.Simple::fromEntity);
            }
            case "webtoons" -> {
                Page<Webtoon> webtoonPage = webtoonRepository.findCompletedMasterpieces(pageable);
                contents = webtoonPage.map(ContentResponse.Simple::fromEntity);
            }
            default -> throw new CustomException(ErrorCode.INVALID_CONTENT_TYPE);

        };

        return contents;
    }

    @ExecutionTimer
    @Transactional(readOnly = true)
    public Page<ContentResponse.Simple> getRecentContents(String contentType, Pageable pageable) {

        Page<ContentResponse.Simple> contents;
        LocalDateTime since = LocalDateTime.now().minusDays(180).with(LocalTime.MIN);
        switch (contentType) {
            case "webnovels" -> {
                Page<Webnovel> webnovelPage = webnovelRepository.findRecentWebnovels(since, pageable);
                contents = webnovelPage.map(ContentResponse.Simple::fromEntity);
            }
            case "webtoons" -> {
                Page<Webtoon> webtoonPage = webtoonRepository.findRecentWebtoons(since, pageable);
                contents = webtoonPage.map(ContentResponse.Simple::fromEntity);
            }
            default -> throw new CustomException(ErrorCode.INVALID_CONTENT_TYPE);
        }


        return contents;

    }

    @ExecutionTimer
    @Transactional(readOnly = true)
    public Map<String, Object> getRecommendKeywordContents(String contentType, Pageable pageable) {
        LocalDate currentDate = LocalDate.now();

        Keyword keyword = keywordRepository.findValidKeyword(currentDate).orElseThrow(
                () -> new CustomException(ErrorCode.INVALID_KEYWORD)
        );

        Page<ContentResponse.Simple> responses;

        switch (contentType) {
            case "webnovels" -> {
                Page<Webnovel> webnovels = webnovelRepository.findByKeywordName(keyword.getName(), pageable);
                responses = webnovels.map(ContentResponse.Simple::fromEntity);
            }
            case "webtoons" -> {
                Page<Webtoon> webtoons = webtoonRepository.findByKeywordName(keyword.getName(), pageable);
                responses = webtoons.map(ContentResponse.Simple::fromEntity);
            }
            default -> throw new CustomException(ErrorCode.INVALID_CONTENT_TYPE);
        }

        Map<String, Object> result = new HashMap<>();
        result.put("keyword", keyword.getName());
        result.put("contents", new PageResponse<>(responses));


        return result;


    }
}
