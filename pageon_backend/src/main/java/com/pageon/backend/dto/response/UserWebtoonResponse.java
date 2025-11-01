package com.pageon.backend.dto.response;

import com.pageon.backend.common.enums.SerialDay;
import com.pageon.backend.common.enums.SeriesStatus;
import com.pageon.backend.entity.Webtoon;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserWebtoonResponse {
    private Long id;
    private String title;
    private String description;
    private String cover;
    private String author;
    private List<UserKeywordResponse> keywords;
    private SerialDay serialDay;
    private SeriesStatus status;
    private Double totalAverageRating;
    private Long totalRatingCount;
    private Long viewCount;
    private String contentType;
    private Boolean isInterested;

    // 에피소드 담기
    private List<EpisodeListResponse> episodes;

    public static UserWebtoonResponse fromEntity(Webtoon webtoon, List<UserKeywordResponse> keywords, List<EpisodeListResponse> episodes, Boolean isInterested) {
        UserWebtoonResponse userWebtoonResponse = new UserWebtoonResponse();
        userWebtoonResponse.setId(webtoon.getId());
        userWebtoonResponse.setTitle(webtoon.getTitle());
        userWebtoonResponse.setDescription(webtoon.getDescription());
        userWebtoonResponse.setCover(webtoon.getCover());
        userWebtoonResponse.setAuthor(webtoon.getCreator().getPenName());
        userWebtoonResponse.setKeywords(keywords);
        userWebtoonResponse.setSerialDay(webtoon.getSerialDay());
        userWebtoonResponse.setStatus(webtoon.getStatus());
        userWebtoonResponse.setTotalAverageRating(webtoon.getTotalAverageRating());
        userWebtoonResponse.setTotalRatingCount(webtoon.getTotalRatingCount());
        userWebtoonResponse.setViewCount(webtoon.getViewCount());
        userWebtoonResponse.setContentType("webtoons");
        userWebtoonResponse.setEpisodes(episodes);
        userWebtoonResponse.setIsInterested(isInterested);

        return userWebtoonResponse;

    }
}
