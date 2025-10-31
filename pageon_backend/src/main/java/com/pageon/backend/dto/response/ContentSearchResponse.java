package com.pageon.backend.dto.response;

import com.pageon.backend.entity.Webnovel;
import com.pageon.backend.entity.Webtoon;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ContentSearchResponse {
    private Long id;
    private String title;
    private String author;
    private String cover;
    private String description;
    private Double totalAverageRating;
    private Long totalRatingCount;
    private List<KeywordResponse> keywords;
    private String contentType;

    public static ContentSearchResponse fromWebnovel(Webnovel webnovel) {

        return ContentSearchResponse.builder()
                .id(webnovel.getId())
                .title(webnovel.getTitle())
                .author(webnovel.getCreator().getPenName())
                .cover(webnovel.getCover())
                .description(webnovel.getDescription())
                .totalAverageRating(webnovel.getTotalAverageRating())
                .totalRatingCount(webnovel.getTotalRatingCount())
                .keywords(webnovel.getKeywords().stream().map(KeywordResponse::fromEntity).collect(Collectors.toList()))
                .contentType("webnovels")
                .build();
    }

    public static ContentSearchResponse fromWebtoon(Webtoon webtoon) {

        return ContentSearchResponse.builder()
                .id(webtoon.getId())
                .title(webtoon.getTitle())
                .author(webtoon.getCreator().getPenName())
                .cover(webtoon.getCover())
                .description(webtoon.getDescription())
                .totalAverageRating(webtoon.getTotalAverageRating())
                .totalRatingCount(webtoon.getTotalRatingCount())
                .keywords(webtoon.getKeywords().stream().map(KeywordResponse::fromEntity).collect(Collectors.toList()))
                .contentType("webtoons")
                .build();
    }
}
