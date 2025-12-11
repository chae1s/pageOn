package com.pageon.backend.dto.response;

import com.pageon.backend.common.enums.ContentType;
import com.pageon.backend.entity.Content;
import com.pageon.backend.entity.Webnovel;
import com.pageon.backend.entity.Webtoon;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
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
    private Integer episodeCount;
    private LocalDateTime episodeUpdatedAt;
    private Double totalAverageRating;
    private Long totalRatingCount;
    private List<KeywordResponse> keywords;
    private String contentType;

    public static ContentSearchResponse fromEntity(Content content, String contentType) {

        return ContentSearchResponse.builder()
                .id(content.getId())
                .title(content.getTitle())
                .author(content.getCreator().getPenName())
                .cover(content.getCover())
                .description(content.getDescription())
                .episodeCount(content.getEpisodeCount())
                .episodeUpdatedAt(content.getEpisodeUpdatedAt())
                .totalAverageRating(content.getTotalAverageRating())
                .totalRatingCount(content.getTotalRatingCount())
                .keywords(content.getKeywords().stream().map(KeywordResponse::fromEntity).collect(Collectors.toList()))
                .contentType(contentType)
                .build();
    }
}
