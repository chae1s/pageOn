package com.pageon.backend.service;

import com.pageon.backend.common.annotation.ExecutionTimer;
import com.pageon.backend.common.enums.SerialDay;
import com.pageon.backend.dto.response.ContentResponse;
import com.pageon.backend.dto.response.PageResponse;
import com.pageon.backend.entity.Content;
import com.pageon.backend.entity.Keyword;
import com.pageon.backend.entity.Webnovel;
import com.pageon.backend.entity.Webtoon;
import com.pageon.backend.exception.CustomException;
import com.pageon.backend.exception.ErrorCode;
import com.pageon.backend.repository.ContentRepository;
import com.pageon.backend.repository.KeywordRepository;
import com.pageon.backend.repository.WebnovelRepository;
import com.pageon.backend.repository.WebtoonRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ContentService {
    private final ContentRepository contentRepository;
    private final WebnovelRepository webnovelRepository;
    private final WebtoonRepository webtoonRepository;
    private final KeywordRepository keywordRepository;

    @Transactional(readOnly = true)
    public Page<ContentResponse.Search> getContentsByTitleOrCreator(String query, Pageable sortedPageable) {

        log.debug("Entering getContentsByTitleOrCreator. Query = [{}], Pageable = {}", query, sortedPageable);
        Page<Content> contents = contentRepository.findByTitleOrPenNameContaining(query, sortedPageable);

        log.info("Content search by title/creator successful. Query: [{}]. Found {} total results.",
                query,
                contents.getTotalElements());

        return contents.map(ContentResponse.Search::fromEntity);
    }

    @ExecutionTimer
    @Transactional(readOnly = true)
    @Cacheable(value = "contents:daily", key = "#contentType + ':' + #serialDay")
    public List<ContentResponse.Simple> getContentsByDate(String serialDay, String contentType) {

        log.info("[DailyContents] No cache found for {}:{} - Fetching from Database", contentType, serialDay);
        Pageable pageable = PageRequest.of(0, 18, Sort.by(Sort.Order.desc("viewCount")));

        switch (contentType) {
            case "webnovels" -> {
                Page<Webnovel> webnovels = webnovelRepository.findDailyRanking(SerialDay.valueOf(serialDay), pageable);

                return webnovels.getContent().stream()
                        .map(ContentResponse.Simple::fromEntity).collect(Collectors.toList());
            }
            case "webtoons" -> {
                Page<Webtoon> webtoons = webtoonRepository.findDailyRanking(SerialDay.valueOf(serialDay), pageable);

                return webtoons.getContent().stream()
                        .map(ContentResponse.Simple::fromEntity).collect(Collectors.toList());
            }
        }

        throw new CustomException(ErrorCode.INVALID_CONTENT_TYPE);

    }


    @ExecutionTimer
    @Transactional(readOnly = true)
    @Cacheable(value = "contents:masterpiece", key = "#contentType")
    public List<ContentResponse.Simple> getMasterpiecesContents(String contentType) {

        log.info("[MasterpieceContents] No cache found for {} - Fetching from Database", contentType);
        Pageable pageable = PageRequest.of(0, 6, Sort.by(Sort.Order.desc("viewCount")));

        switch (contentType) {
            case "all" -> {
                Page<Content> contents = contentRepository.findCompletedMasterpieces(pageable);

                return contents.stream().map(ContentResponse.Simple::fromEntity).collect(Collectors.toList());
            }
            case "webnovels" -> {
                Page<Webnovel> webnovels = webnovelRepository.findCompletedMasterpieces(pageable);

                return webnovels.stream().map(ContentResponse.Simple::fromEntity).collect(Collectors.toList());
            }
            case "webtoons" -> {
                Page<Webtoon> webtoons = webtoonRepository.findCompletedMasterpieces(pageable);

                return webtoons.stream().map(ContentResponse.Simple::fromEntity).collect(Collectors.toList());
            }

        };

        throw new CustomException(ErrorCode.INVALID_CONTENT_TYPE);
    }

    @ExecutionTimer
    @Transactional(readOnly = true)
    public Page<ContentResponse.Simple> getMasterpiecesContents(String contentType, Pageable pageable) {

        switch (contentType) {
            case "all" -> {
                Page<Content> contents = contentRepository.findCompletedMasterpieces(pageable);

                return contents.map(ContentResponse.Simple::fromEntity);
            }
            case "webnovels" -> {
                Page<Webnovel> webnovels = webnovelRepository.findCompletedMasterpieces(pageable);

                return webnovels.map(ContentResponse.Simple::fromEntity);
            }
            case "webtoons" -> {
                Page<Webtoon> webtoons = webtoonRepository.findCompletedMasterpieces(pageable);

                return webtoons.map(ContentResponse.Simple::fromEntity);
            }

        };

        throw new CustomException(ErrorCode.INVALID_CONTENT_TYPE);
    }


    @ExecutionTimer
    @Transactional(readOnly = true)
    @Cacheable(value = "contents:recent", key = "#contentType + ':' + #date")
    public List<ContentResponse.Simple> getRecentContents(String contentType, LocalDate date) {

        log.info("[RecentContents] No cache found for {}:{} - Fetching from Database", contentType, date);
        Pageable pageable = PageRequest.of(0, 6, Sort.by(Sort.Order.desc("createdAt")));

        LocalDateTime since = date.minusDays(180).atStartOfDay();

        switch (contentType) {
            case "webnovels" -> {
                Page<Webnovel> webnovels = webnovelRepository.findRecentWebnovels(since, pageable);
                return webnovels.stream().map(ContentResponse.Simple::fromEntity).collect(Collectors.toList());
            }
            case "webtoons" -> {
                Page<Webtoon> webtoons = webtoonRepository.findRecentWebtoons(since, pageable);
                return webtoons.stream().map(ContentResponse.Simple::fromEntity).collect(Collectors.toList());
            }
        }

        throw new CustomException(ErrorCode.INVALID_CONTENT_TYPE);

    }

    @ExecutionTimer
    @Transactional(readOnly = true)
    public Page<ContentResponse.Simple> getRecentContents(String contentType, Pageable pageable) {

        LocalDateTime since = LocalDate.now().minusDays(180).atStartOfDay();

        switch (contentType) {
            case "webnovels" -> {
                Page<Webnovel> webnovels = webnovelRepository.findRecentWebnovels(since, pageable);
                return webnovels.map(ContentResponse.Simple::fromEntity);
            }
            case "webtoons" -> {
                Page<Webtoon> webtoons = webtoonRepository.findRecentWebtoons(since, pageable);
                return webtoons.map(ContentResponse.Simple::fromEntity);
            }
        }

        throw new CustomException(ErrorCode.INVALID_CONTENT_TYPE);

    }

    @ExecutionTimer
    @Transactional(readOnly = true)
    @Cacheable(value = "contents:keyword", key = "#contentType")
    public Map<String, Object> getRecommendKeywordContents(String contentType) {

        log.info("[KeywordContents] No cache found for {} - Fetching from Database", contentType);
        LocalDate currentDate = LocalDate.now();
        Pageable pageable = PageRequest.of(0, 6, Sort.by(Sort.Order.desc("viewCount")));

        Keyword keyword = keywordRepository.findValidKeyword(currentDate).orElseThrow(
                () -> new CustomException(ErrorCode.INVALID_KEYWORD)
        );

        List<ContentResponse.Simple> contents;

        switch (contentType) {
            case "webnovels" -> {
                Page<Webnovel> webnovels = webnovelRepository.findByKeywordName(keyword.getName(), pageable);
                contents = webnovels.stream().map(ContentResponse.Simple::fromEntity).collect(Collectors.toList());

            }
            case "webtoons" -> {
                Page<Webtoon> webtoons = webtoonRepository.findByKeywordName(keyword.getName(), pageable);
                contents = webtoons.stream().map(ContentResponse.Simple::fromEntity).collect(Collectors.toList());
            }
            default -> throw new CustomException(ErrorCode.INVALID_CONTENT_TYPE);
        }

        Map<String, Object> result = new HashMap<>();
        result.put("keyword", keyword.getName());
        result.put("contents", contents);

        return result;

    }

    @ExecutionTimer
    @Transactional(readOnly = true)
    public Map<String, Object> getRecommendKeywordContents(String contentType, Pageable pageable) {

        LocalDate currentDate = LocalDate.now();

        Keyword keyword = keywordRepository.findValidKeyword(currentDate).orElseThrow(
                () -> new CustomException(ErrorCode.INVALID_KEYWORD)
        );

        Page<ContentResponse.Simple> contents;

        switch (contentType) {
            case "webnovels" -> {
                Page<Webnovel> webnovels = webnovelRepository.findByKeywordName(keyword.getName(), pageable);
                contents = webnovels.map(ContentResponse.Simple::fromEntity);

            }
            case "webtoons" -> {
                Page<Webtoon> webtoons = webtoonRepository.findByKeywordName(keyword.getName(), pageable);
                contents = webtoons.map(ContentResponse.Simple::fromEntity);
            }
            default -> throw new CustomException(ErrorCode.INVALID_CONTENT_TYPE);
        }

        return Map.of(
                "keyword", keyword.getName(),
                "contents", new PageResponse<>(contents)
        );

    }

}
