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
    private Long prevEpisodeId;
    private Long nextEpisodeId;

    public static WebnovelEpisodeDetailResponse fromEntity(Long id, String title, Integer episodeNum, String episodeTitle, String content, Long prevEpisodeId, Long nextEpisodeId) {
        return new WebnovelEpisodeDetailResponse(
                id,
                title,
                episodeNum,
                episodeTitle,
                content,
                prevEpisodeId,
                nextEpisodeId
        );
    }
}
