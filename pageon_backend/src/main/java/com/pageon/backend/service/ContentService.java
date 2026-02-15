package com.pageon.backend.service;

import com.pageon.backend.common.annotation.ExecutionTimer;
import com.pageon.backend.common.enums.SerialDay;
import com.pageon.backend.dto.response.ContentResponse;
import com.pageon.backend.dto.response.PageResponse;
import com.pageon.backend.dto.token.TokenInfo;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
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
    private final RedisTemplate<String, Object> redisTemplate;

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
    public List<ContentResponse.Simple> getContentsByDate(String serialDay, String contentType) {

        String key = String.format("contents:daily:%s:%s", contentType, serialDay);


        try {
            List<ContentResponse.Simple> contents = (List<ContentResponse.Simple>) redisTemplate.opsForValue().get(key);

            if (contents == null) {
                return fetchContentsFromDbByDate(serialDay, contentType);
            }

            return contents;

        } catch (Exception e) {
            log.error("REDIS 작업 중 에러 발생: ", e);
            return fetchContentsFromDbByDate(serialDay, contentType);
        }

    }

    private List<ContentResponse.Simple> fetchContentsFromDbByDate(String serialDay, String contentType) {
        Pageable pageable = PageRequest.of(0, 18, Sort.by(Sort.Order.desc("viewCount")));

        String keyName = String.format("contents:daily:%s:%s", contentType, serialDay);

        log.info("요일별 콘텐츠 데이터 DB 조회");
        switch (contentType) {
            case "webnovels" -> {
                Page<Webnovel> webnovels = webnovelRepository.findDailyRanking(SerialDay.valueOf(serialDay), pageable);
                List<ContentResponse.Simple> contents = webnovels.getContent().stream()
                        .map(ContentResponse.Simple::fromEntity).collect(Collectors.toList());

                redisTemplate.opsForValue().set(keyName, contents);

                return contents;
            }
            case "webtoons" -> {
                Page<Webtoon> webtoons = webtoonRepository.findDailyRanking(SerialDay.valueOf(serialDay), pageable);
                List<ContentResponse.Simple> contents = webtoons.getContent().stream()
                        .map(ContentResponse.Simple::fromEntity).collect(Collectors.toList());
                redisTemplate.opsForValue().set(keyName, contents);
            }
        }

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
    public Page<ContentResponse.Simple> getRecentContents(String contentType, Pageable pageable) {

        LocalDateTime since = LocalDateTime.now().minusDays(180).with(LocalTime.MIN);
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

    @ExecutionTimer
    @Scheduled(cron = "0 3 15 * * *")
    public void saveDailyContents() {
        Pageable pageable = PageRequest.of(0, 18, Sort.by(Sort.Order.desc("viewCount")));

        saveDailyWebnovels(pageable);
        saveDailyWebtoons(pageable);

    }

    private void saveDailyWebnovels(Pageable pageable) {


        for (SerialDay serialDay : SerialDay.values()) {
            String keyName = String.format("contents:daily:webnovels:%s", serialDay.name());
            Page<Webnovel> webnovels = webnovelRepository.findDailyRanking(serialDay, pageable);

            List<ContentResponse.Simple> contents = webnovels.getContent().stream()
                    .map(ContentResponse.Simple::fromEntity)
                    .collect(Collectors.toList());

            redisTemplate.opsForValue().set(keyName, contents);
        }
        
    }

    private void saveDailyWebtoons(Pageable pageable) {

        for (SerialDay serialDay : SerialDay.values()) {

            String keyName = String.format("contents:daily:webtoons:%s", serialDay.name());

            Page<Webtoon> webtoons = webtoonRepository.findDailyRanking(serialDay, pageable);
            List<ContentResponse.Simple> contents = webtoons.getContent().stream()
                    .map(ContentResponse.Simple::fromEntity)
                    .collect(Collectors.toList());


            redisTemplate.opsForValue().set(keyName, contents);
        }

    }



}
