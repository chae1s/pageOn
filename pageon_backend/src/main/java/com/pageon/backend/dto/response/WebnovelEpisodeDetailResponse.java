package com.pageon.backend.dto.response;

import com.pageon.backend.entity.WebnovelEpisode;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WebnovelEpisodeDetailResponse {
    private Long id;
    private String title;
    private Integer episodeNum;
    private String episodeTitle;
    private String content;
    private Double averageRating;
    private Long ratingCount;
    private Long prevEpisodeId;
    private Long nextEpisodeId;

    public static WebnovelEpisodeDetailResponse fromEntity(WebnovelEpisode webnovelEpisode, String title, Long prevEpisodeId, Long nextEpisodeId) {
        return new WebnovelEpisodeDetailResponse(
                webnovelEpisode.getId(),
                title,
                webnovelEpisode.getEpisodeNum(),
                webnovelEpisode.getEpisodeTitle(),
                webnovelEpisode.getContent(),
                webnovelEpisode.getAverageRating(),
                webnovelEpisode.getRatingCount(),
                prevEpisodeId,
                nextEpisodeId
        );
    }
}
