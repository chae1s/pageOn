package com.pageon.backend.dto.response;

import com.pageon.backend.entity.WebnovelEpisode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
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
    private Integer userScore;
    private BestCommentResponse bestComment;

    public static WebnovelEpisodeDetailResponse fromEntity(
            WebnovelEpisode webnovelEpisode, String title,
            Long prevEpisodeId, Long nextEpisodeId, Integer userScore, BestCommentResponse bestComment)
    {
        return WebnovelEpisodeDetailResponse.builder()
                .id(webnovelEpisode.getId())
                .title(title)
                .episodeNum(webnovelEpisode.getEpisodeNum())
                .episodeTitle(webnovelEpisode.getEpisodeTitle())
                .content(webnovelEpisode.getContent())
                .averageRating(webnovelEpisode.getAverageRating())
                .ratingCount(webnovelEpisode.getRatingCount())
                .prevEpisodeId(prevEpisodeId)
                .nextEpisodeId(nextEpisodeId)
                .userScore(userScore)
                .bestComment(bestComment)
                .build();
    }
}
