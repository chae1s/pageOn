package com.pageon.backend.dto.response;

import com.pageon.backend.entity.WebtoonEpisode;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WebtoonEpisodeDetailResponse {
    private Long id;
    private String title;
    private Integer episodeNum;
    private Double averageRating;
    private Long ratingCount;
    private List<WebtoonImagesResponse> images;
    private Long prevEpisodeId;
    private Long nextEpisodeId;

    public static WebtoonEpisodeDetailResponse fromEntity(WebtoonEpisode episode, List<WebtoonImagesResponse> images, Long prevEpisodeId, Long nextEpisodeId) {
        return new WebtoonEpisodeDetailResponse(
                episode.getId(),
                episode.getEpisodeTitle(),
                episode.getEpisodeNum(),
                episode.getAverageRating(),
                episode.getRatingCount(),
                images,
                prevEpisodeId,
                nextEpisodeId
        );
    }
}
