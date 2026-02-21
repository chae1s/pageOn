package com.pageon.backend.service;

import com.pageon.backend.common.enums.SerialDay;
import com.pageon.backend.dto.response.ContentResponse;
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
import org.springframework.cache.annotation.CachePut;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ContentCacheService {

    private final ContentRepository contentRepository;
    private final WebnovelRepository webnovelRepository;
    private final WebtoonRepository webtoonRepository;
    private final KeywordRepository keywordRepository;

    @CachePut(value = "contents:daily", key = "'webnovels:' + #serialDay")
    public List<ContentResponse.Simple> refreshDailyWebnovels(Pageable pageable, SerialDay serialDay) {

        log.info("[DailyContents] Redis cache WARM-UP starting for webnovels: {}", serialDay);
        Page<Webnovel> webnovels = webnovelRepository.findDailyRanking(serialDay, pageable);

        return webnovels.getContent().stream()
                .map(ContentResponse.Simple::fromEntity)
                .collect(Collectors.toList());

    }

    @CachePut(value = "contents:daily", key = "'webtoons:' + #serialDay")
    public List<ContentResponse.Simple> refreshDailyWebtoons(Pageable pageable, SerialDay serialDay) {

        log.info("[DailyContents] Redis cache WARM-UP starting for webtoons: {}", serialDay);
        Page<Webtoon> webtoons = webtoonRepository.findDailyRanking(serialDay, pageable);

        return webtoons.getContent().stream()
                .map(ContentResponse.Simple::fromEntity)
                .collect(Collectors.toList());

    }

    @CachePut(value = "contents:masterpiece", key = "'all'")
    public List<ContentResponse.Simple> refreshMasterpiecesAll(Pageable pageable) {

        log.info("[MasterpieceContents] Redis cache WARM-UP starting for all");
        Page<Content> contents = contentRepository.findCompletedMasterpieces(pageable);

        return contents.stream()
                .map(ContentResponse.Simple::fromEntity)
                .collect(Collectors.toList());
    }

    @CachePut(value = "contents:masterpiece", key = "'webnovels'")
    public List<ContentResponse.Simple> refreshMasterpiecesWebnovels(Pageable pageable) {

        log.info("[MasterpieceContents] Redis cache WARM-UP starting for webnovels");
        Page<Webnovel> webnovels = webnovelRepository.findCompletedMasterpieces(pageable);

        return webnovels.stream()
                .map(ContentResponse.Simple::fromEntity)
                .collect(Collectors.toList());
    }

    @CachePut(value = "contents:masterpiece", key = "'webtoons'")
    public List<ContentResponse.Simple> refreshMasterpiecesWebtoons(Pageable pageable) {

        log.info("[MasterpieceContents] Redis cache WARM-UP starting for webtoons");
        Page<Webtoon> webtoons = webtoonRepository.findCompletedMasterpieces(pageable);

        return webtoons.stream()
                .map(ContentResponse.Simple::fromEntity)
                .collect(Collectors.toList());
    }

    @CachePut(value = "contents:keyword", key = "'webnovels'")
    public Map<String, Object> refreshKeywordWebnovels(Pageable pageable) {

        log.info("[KeywordContents] Redis cache WARM-UP starting for webnovels");
        LocalDate date = LocalDate.now();
        Keyword keyword = keywordRepository.findValidKeyword(date).orElseThrow(
                () -> new CustomException(ErrorCode.INVALID_KEYWORD)
        );

        Page<Webnovel> webnovels = webnovelRepository.findByKeywordName(keyword.getName(), pageable);


        return Map.of(
                "keyword", keyword.getName(),
                "contents", webnovels.stream().map(ContentResponse.Simple::fromEntity).collect(Collectors.toList())
        );

    }

    @CachePut(value = "contents:keyword", key = "'webtoons'")
    public Map<String, Object> refreshKeywordWebtoons(Pageable pageable) {

        log.info("[KeywordContents] Redis cache WARM-UP starting for webtoons");
        LocalDate date = LocalDate.now();
        Keyword keyword = keywordRepository.findValidKeyword(date).orElseThrow(
                () -> new CustomException(ErrorCode.INVALID_KEYWORD)
        );

        Page<Webtoon> webtoons = webtoonRepository.findByKeywordName(keyword.getName(), pageable);


        return Map.of(
                "keyword", keyword.getName(),
                "contents", webtoons.stream().map(ContentResponse.Simple::fromEntity).collect(Collectors.toList())
        );
    }

    @CachePut(value = "contents:recent", key = "'webnovels:' + #date")
    public List<ContentResponse.Simple> refreshRecentWebnovels(Pageable pageable, LocalDate date) {

        log.info("[RecentContents] Redis cache WARM-UP starting for webnovels:{}", date);
        LocalDateTime since = date.minusDays(180).atStartOfDay();

        Page<Webnovel> webnovels = webnovelRepository.findRecentWebnovels(since, pageable);

        return webnovels.stream()
                .map(ContentResponse.Simple::fromEntity)
                .collect(Collectors.toList());
    }

    @CachePut(value = "contents:recent", key = "'webntoons:' + #date")
    public List<ContentResponse.Simple> refreshRecentWebtoons(Pageable pageable, LocalDate date) {

        log.info("[RecentContents] Redis cache WARM-UP starting for webtoons:{}", date);
        LocalDateTime since = date.minusDays(180).atStartOfDay();

        Page<Webtoon> webtoons = webtoonRepository.findRecentWebtoons(since, pageable);

        return webtoons.stream()
                .map(ContentResponse.Simple::fromEntity)
                .collect(Collectors.toList());
    }
}
