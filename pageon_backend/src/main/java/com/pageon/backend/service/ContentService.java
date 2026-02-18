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
    public List<ContentResponse.Simple> getMasterpiecesContents(String contentType) {

        String keyName = String.format("contents:masterpiece:%s", contentType);
        try {
            List<ContentResponse.Simple> contents = (List<ContentResponse.Simple>) redisTemplate.opsForValue().get(keyName);

            if (contents == null) {
                return fetchContentsFromDbByMasterpiece(contentType);
            }

            return contents;
        } catch (Exception e) {
            return fetchContentsFromDbByMasterpiece(contentType);
        }

    }

    private List<ContentResponse.Simple> fetchContentsFromDbByMasterpiece(String contentType) {
        Pageable pageable = PageRequest.of(0, 6, Sort.by(Sort.Order.desc("viewCount")));

        String keyName = String.format("contents:masterpiece:%s", contentType);

        switch (contentType) {
            case "all" -> {
                Page<Content> contents = contentRepository.findCompletedMasterpieces(pageable);
                List<ContentResponse.Simple> contentList = contents.stream().map(ContentResponse.Simple::fromEntity).collect(Collectors.toList());

                redisTemplate.opsForValue().set(keyName, contentList);
                return contentList;
            }
            case "webnovels" -> {
                Page<Webnovel> webnovels = webnovelRepository.findCompletedMasterpieces(pageable);
                List<ContentResponse.Simple> webnovelList = webnovels.stream().map(ContentResponse.Simple::fromEntity).collect(Collectors.toList());

                redisTemplate.opsForValue().set(keyName, webnovelList);
                return webnovelList;
            }
            case "webtoons" -> {
                Page<Webtoon> webtoons = webtoonRepository.findCompletedMasterpieces(pageable);
                List<ContentResponse.Simple> webtoonList = webtoons.stream().map(ContentResponse.Simple::fromEntity).collect(Collectors.toList());

                redisTemplate.opsForValue().set(keyName, webtoonList);
                return webtoonList;
            }

        };

        throw new CustomException(ErrorCode.INVALID_CONTENT_TYPE);
    }

    @ExecutionTimer
    @Transactional(readOnly = true)
    public List<ContentResponse.Simple> getRecentContents(String contentType) {

        LocalDate now = LocalDate.now();

        String keyName = String.format("contents:recent:%s:%s", contentType, now);

        try {
            List<ContentResponse.Simple> contents = (List<ContentResponse.Simple>) redisTemplate.opsForValue().get(keyName);

            if (contents == null) {
                return fetchContentsFromDbByRecent(contentType);
            }

            return contents;
        } catch (Exception e) {
            return fetchContentsFromDbByRecent(contentType);
        }


    }

    private List<ContentResponse.Simple> fetchContentsFromDbByRecent(String contentType) {
        Pageable pageable = PageRequest.of(0, 6, Sort.by(Sort.Order.desc("createdAt")));

        LocalDateTime since = LocalDateTime.now().minusDays(180).with(LocalTime.MIN);
        LocalDate now = LocalDate.now();

        String keyName = String.format("contents:recent:%s:%s", contentType, now);
        switch (contentType) {
            case "webnovels" -> {
                Page<Webnovel> webnovels = webnovelRepository.findRecentWebnovels(since, pageable);
                List<ContentResponse.Simple> webnovelList = webnovels.stream().map(ContentResponse.Simple::fromEntity).collect(Collectors.toList());

                redisTemplate.opsForValue().set(keyName, webnovelList);
                return webnovelList;
            }
            case "webtoons" -> {
                Page<Webtoon> webtoons = webtoonRepository.findRecentWebtoons(since, pageable);
                List<ContentResponse.Simple> webtoonList = webtoons.stream().map(ContentResponse.Simple::fromEntity).collect(Collectors.toList());

                redisTemplate.opsForValue().set(keyName, webtoonList);
                return webtoonList;
            }
        }

        throw new CustomException(ErrorCode.INVALID_CONTENT_TYPE);

    }


    @ExecutionTimer
    @Transactional(readOnly = true)
    public Map<String, Object> getRecommendKeywordContents(String contentType) {

        String keyName = String.format("contents:keyword:%s", contentType);
        try {
            Map<String, Object> contents = (Map<String, Object>) redisTemplate.opsForValue().get(keyName);

            if (contents == null) {
                log.info("데이터 없음");
                return fetchContentsFromDbByKeyword(contentType);
            }

            return contents;
        } catch (Exception e) {
            log.error("REDIS 작업 중 에러 발생: ", e);
            return fetchContentsFromDbByKeyword(contentType);
        }

    }

    private Map<String, Object> fetchContentsFromDbByKeyword(String contentType) {
        LocalDate currentDate = LocalDate.now();
        Pageable pageable = PageRequest.of(0, 6, Sort.by(Sort.Order.desc("viewCount")));

        log.info("here");
        Keyword keyword = keywordRepository.findValidKeyword(currentDate).orElseThrow(
                () -> new CustomException(ErrorCode.INVALID_KEYWORD)
        );

        List<ContentResponse.Simple> contents;

        String keyName = String.format("contents:keyword:%s", contentType);

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

        redisTemplate.opsForValue().set(keyName, result);

        return result;

    }

    @ExecutionTimer
    @Scheduled(cron = "0 50 23 * * 0")
    public void saveContentsInRedis() {
        Pageable dailyPageable = PageRequest.of(0, 18, Sort.by(Sort.Order.desc("viewCount")));
        Pageable recommendationPageable = PageRequest.of(0, 6, Sort.by(Sort.Order.desc("viewCount")));

        LocalDate currentDate = LocalDate.now();

        Keyword keyword = keywordRepository.findValidKeyword(currentDate).orElseThrow(
                () -> new CustomException(ErrorCode.INVALID_KEYWORD)
        );

        saveDailyWebnovels(dailyPageable);
        saveDailyWebtoons(dailyPageable);
        saveMasterpiecesAll(recommendationPageable);
        saveMasterpiecesWebnovels(recommendationPageable);
        saveMasterpiecesWebtoons(recommendationPageable);
        saveRecommendKeywordWebnovels(recommendationPageable, keyword.getName());
        saveRecommendKeywordWebtoons(recommendationPageable, keyword.getName());

    }

    @ExecutionTimer
    @Scheduled(cron = "0 50 23 * * *")
    public void saveContents() {
        Pageable pageable = PageRequest.of(0, 6, Sort.by(Sort.Order.desc("createdAt")));
        LocalDateTime since = LocalDateTime.now().minusDays(180).with(LocalTime.MIN);


        saveRecentWebnovels(pageable, since);
        saveRecentWebtoons(pageable, since);
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

    private void saveMasterpiecesAll(Pageable pageable) {

        Page<Content> contents = contentRepository.findCompletedMasterpieces(pageable);
        redisTemplate.opsForValue().set(
                "contents:masterpiece:all",
                contents.stream().map(ContentResponse.Simple::fromEntity).collect(Collectors.toList())
        );

    }

    private void saveMasterpiecesWebnovels(Pageable pageable) {
        Page<Webnovel> webnovels = webnovelRepository.findCompletedMasterpieces(pageable);
        redisTemplate.opsForValue().set(
                "contents:masterpiece:webnovels",
                webnovels.stream().map(ContentResponse.Simple::fromEntity).collect(Collectors.toList())
        );
    }

    private void saveMasterpiecesWebtoons(Pageable pageable) {
        Page<Webtoon> webtoons = webtoonRepository.findCompletedMasterpieces(pageable);
        redisTemplate.opsForValue().set(
                "contents:masterpiece:webtoons",
                webtoons.stream().map(ContentResponse.Simple::fromEntity).collect(Collectors.toList())
        );
    }
    
    private void saveRecentWebnovels(Pageable pageable, LocalDateTime since) {

        LocalDate keyDate = LocalDate.now().plusDays(1);
        String keyName = String.format("contents:recent:webnovels:%s", keyDate);
        
        Page<Webnovel> webnovels = webnovelRepository.findRecentWebnovels(since, pageable);
        redisTemplate.opsForValue().set(
                keyName,
                webnovels.stream().map(ContentResponse.Simple::fromEntity).collect(Collectors.toList())
        );
    }
    
    private void saveRecentWebtoons(Pageable pageable, LocalDateTime since) {
        LocalDate keyDate = LocalDate.now().plusDays(1);

        String keyName = String.format("contents:recent:webtoons:%s", keyDate);
        Page<Webtoon> webtoons = webtoonRepository.findRecentWebtoons(since, pageable);
        redisTemplate.opsForValue().set(
                keyName,
                webtoons.stream().map(ContentResponse.Simple::fromEntity).collect(Collectors.toList())
        );
    }

    private void saveRecommendKeywordWebnovels(Pageable pageable, String keywordName) {

        Page<Webnovel> webnovels = webnovelRepository.findByKeywordName(keywordName, pageable);

        Map<String, Object> result = new HashMap<>();

        result.put("keyword", keywordName);
        result.put(
                "contents",
                webnovels.stream().map(ContentResponse.Simple::fromEntity).collect(Collectors.toList())
        );

        redisTemplate.opsForValue().set("contents:keyword:webnovels", result);

    }

    private void saveRecommendKeywordWebtoons(Pageable pageable, String keywordName) {

        Page<Webtoon> webtoons = webtoonRepository.findByKeywordName(keywordName, pageable);

        Map<String, Object> result = new HashMap<>();

        result.put("keyword", keywordName);
        result.put(
                "contents",
                webtoons.stream().map(ContentResponse.Simple::fromEntity).collect(Collectors.toList())
        );

        redisTemplate.opsForValue().set("contents:keyword:webtoons", result);
    }


}
