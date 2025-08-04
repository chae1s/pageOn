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
    private Double rating;
    private Integer ratingCount;
    private Long viewCount;

    // 에피소드 담기


    public static UserWebtoonResponse fromEntity(Webtoon webtoon, List<UserKeywordResponse> keywords) {
        UserWebtoonResponse userWebtoonResponse = new UserWebtoonResponse();
        userWebtoonResponse.setId(webtoon.getId());
        userWebtoonResponse.setTitle(webtoon.getTitle());
        userWebtoonResponse.setDescription(webtoon.getDescription());
        userWebtoonResponse.setCover(webtoon.getCover());
        userWebtoonResponse.setAuthor(webtoon.getCreator().getPenName());
        userWebtoonResponse.setKeywords(keywords);
        userWebtoonResponse.setSerialDay(webtoon.getSerialDay());
        userWebtoonResponse.setStatus(webtoon.getStatus());
        userWebtoonResponse.setRating(4.9231);
        userWebtoonResponse.setRatingCount(125039);
        userWebtoonResponse.setViewCount(webtoon.getViewCount());

        return userWebtoonResponse;

    }
}
