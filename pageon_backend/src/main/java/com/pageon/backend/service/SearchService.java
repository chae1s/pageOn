package com.pageon.backend.service;

import com.pageon.backend.common.utils.PageableUtil;
import com.pageon.backend.dto.response.ContentResponse;
import com.pageon.backend.exception.CustomException;
import com.pageon.backend.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class SearchService {

    private final UserWebnovelService userWebnovelService;
    private final UserWebtoonService userWebtoonService;
    private final ContentService contentService;


    @Transactional(readOnly = true)
    public Page<ContentResponse.Search> getContentsByKeyword(String contentType, String query, String sort, Pageable pageable) {
        Pageable sortedPageable = PageableUtil.createContentPageable(pageable, sort);

        switch (contentType) {
            case "webnovels" -> {
                return userWebnovelService.getWebnovelsByKeyword(query, sortedPageable);
            }
            case "webtoons" -> {
                return userWebtoonService.getWebtoonsByKeyword(query, sortedPageable);
            }
        }

        throw new CustomException(ErrorCode.INVALID_CONTENT_TYPE);

    }

    @Transactional(readOnly = true)
    public Page<ContentResponse.Search> getContentsByTitleOrCreator(String contentType, String query, String sort, Pageable pageable) {

        Pageable sortedPageable = PageableUtil.createContentPageable(pageable, sort);

        switch (contentType) {
            case "webnovels" -> {
                return userWebnovelService.getWebnovelsByTitleOrCreator(query, sortedPageable);
            }
            case "webtoons" -> {
                return userWebtoonService.getWebtoonsByTitleOrCreator(query, sortedPageable);
            }
            case "all" -> {
                return contentService.getContentsByTitleOrCreator(query, sortedPageable);
            }
        }

        throw new CustomException(ErrorCode.INVALID_CONTENT_TYPE);

    }
}
