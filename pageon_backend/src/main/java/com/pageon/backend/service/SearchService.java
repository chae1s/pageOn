package com.pageon.backend.service;

import com.pageon.backend.dto.response.ContentSearchResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class SearchService {

    private final UserWebnovelService userWebnovelService;
    private final UserWebtoonService userWebtoonService;


    public Page<ContentSearchResponse> getContentsByTitleOrCreator(String query, String sort, Pageable pageable) {

        log.debug("Entering 'searchIntegrated'. Query: [{}], Sort: [{}], Page: {}",
                query, sort, pageable.getPageNumber());
        Page<ContentSearchResponse> webnovelPage = userWebnovelService.getWebnovelsByTitleOrCreator(query, sort, pageable);

        Page<ContentSearchResponse> webtoonPage = userWebtoonService.getWebtoonsByTitleOrCreator(query, sort, pageable);

        log.debug("Fetched {} webtoons (Total: {}) and {} webnovels (Total: {}).",
                webtoonPage.getNumberOfElements(), webtoonPage.getTotalElements(),
                webnovelPage.getNumberOfElements(), webnovelPage.getTotalElements());

        List<ContentSearchResponse> combinedList = new ArrayList<>(webnovelPage.getContent());
        combinedList.addAll(webtoonPage.getContent());

        Comparator<ContentSearchResponse> comparator = Comparator.comparing(ContentSearchResponse::getTitle);
        combinedList.sort(comparator);

        List<ContentSearchResponse> finalList = combinedList.stream().limit(pageable.getPageSize()).toList();

        long totalElements = webnovelPage.getTotalElements() + webtoonPage.getTotalElements();

        log.info("[IntegratedSearch] 통합 검색 성공. Query: [{}], Sort: [{}]. Found {} total results.",
                query, sort, totalElements);
        return new PageImpl<>(finalList, pageable, totalElements);
    }
}
