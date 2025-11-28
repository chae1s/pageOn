package com.pageon.backend.dto.response;

import com.pageon.backend.entity.WebtoonEpisode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WebtoonEpisodeDetailResponse {
    private Long id;
    private String title;
    private String episodeTitle;
    private Integer episodeNum;
    private Integer purchasePrice;
    private Integer rentalPrice;
    private Double averageRating;
    private Long ratingCount;
    private List<WebtoonImagesResponse> images;
    private Long prevEpisodeId;
    private Long nextEpisodeId;
    private Integer userScore;
    private BestCommentResponse bestComment;

    public static WebtoonEpisodeDetailResponse fromEntity(
            WebtoonEpisode episode, String title, List<WebtoonImagesResponse> images,
            Long prevEpisodeId, Long nextEpisodeId, Integer userScore, BestCommentResponse bestComment)
    {
        return WebtoonEpisodeDetailResponse.builder()
                .id(episode.getId())
                .title(title)
                .episodeTitle(episode.getEpisodeTitle())
                .episodeNum(episode.getEpisodeNum())
                .purchasePrice(episode.getPurchasePrice())
                .rentalPrice(episode.getRentalPrice())
                .averageRating(episode.getAverageRating())
                .ratingCount(episode.getRatingCount())
                .images(images)
                .prevEpisodeId(prevEpisodeId)
                .nextEpisodeId(nextEpisodeId)
                .userScore(userScore)
                .bestComment(bestComment)
                .build();
    }
}
