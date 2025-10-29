package com.pageon.backend.common.utils;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

public class PageableUtil {

    public static Pageable createPageable(Pageable pageable, String sort) {
        Sort sortOrder = switch (sort) {
            case "latest" -> Sort.by(Sort.Order.asc("rating"));
            case "rating" -> Sort.by(Sort.Order.asc("title"));
            default -> Sort.by(Sort.Order.asc("viewCount"));
        };

        return PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sortOrder);
    }

    public static Pageable createMyPagePageable(Pageable pageable, String sort) {

        Sort sortOrder = switch (sort) {
            // [TODO] 최근에 읽은 순, 업데이트 순으로 sort 기준 변경
            case "last_read" -> Sort.by(Sort.Order.asc("id"));
            default -> Sort.by(Sort.Order.asc("createdAt"));
        };

        return PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sortOrder);
    }
}
