package com.pageon.backend.common.utils;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

public class PageableUtil {

    public static Pageable createContentPageable(Pageable pageable, String sort) {
        Sort sortOrder = switch (sort) {
            case "latest" -> Sort.by(Sort.Order.desc("episodeUpdatedAt"));
            case "rating" -> Sort.by(Sort.Order.desc("totalAverageRating"));
            default -> Sort.by(Sort.Order.desc("viewCount"));
        };

        return PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sortOrder);
    }

    public static Pageable createInterestPageable(Pageable pageable, String sort) {
        Sort sortOrder = switch (sort) {
            case "update" -> Sort.by(Sort.Order.desc("c.episodeUpdatedAt"));
            case "title" -> Sort.by(Sort.Order.asc("c.title"));
            default -> Sort.by(Sort.Order.desc("i.createdAt"));
        };

        return PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sortOrder);
    }

    public static Pageable createMyPagePageable(Pageable pageable, String sort) {

        Sort sortOrder = switch (sort) {
            // [TODO] 최근에 읽은 순, 업데이트 순으로 sort 기준 변경
            case "last_read" -> Sort.by(Sort.Order.asc("h.lastReadAt"));
            default -> Sort.by(Sort.Order.asc("w.episodeUpdatedAt"));
        };

        return PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sortOrder);
    }

    public static Pageable createCommentPageable(Pageable pageable, String sort) {
        Sort sortOrder = switch (sort) {
            case "latest" -> Sort.by(Sort.Order.desc("createdAt"));
            default -> Sort.by(Sort.Order.desc("likeCount"));
        };

        return PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sortOrder);
    }

    public static Pageable createCreatedAtPageable(Pageable pageable) {
        Sort sortOrder = Sort.by(Sort.Order.desc("createdAt"));

        return PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sortOrder);
    }


}
