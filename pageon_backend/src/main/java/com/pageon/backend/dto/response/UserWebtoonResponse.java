package com.pageon.backend.dto.response;

import com.pageon.backend.common.enums.DayOfWeek;
import com.pageon.backend.common.enums.SeriesStatus;
import com.pageon.backend.entity.Creator;
import com.pageon.backend.entity.Keyword;
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
    private String coverUrl;
    private String penName;
    private List<UserKeywordResponse> keywords;
    private DayOfWeek serialDay;
    private SeriesStatus status;
    private Long viewCount;

    // 에피소드 담기


    public static UserWebtoonResponse fromEntity(Webtoon webtoon, List<UserKeywordResponse> keywords) {
        UserWebtoonResponse userWebtoonResponse = new UserWebtoonResponse();
        userWebtoonResponse.setId(webtoon.getId());
        userWebtoonResponse.setTitle(webtoon.getTitle());
        userWebtoonResponse.setDescription(webtoon.getDescription());
        userWebtoonResponse.setCoverUrl(webtoon.getCover());
        userWebtoonResponse.setPenName(webtoon.getCreator().getPenName());
        userWebtoonResponse.setKeywords(keywords);
        userWebtoonResponse.setSerialDay(webtoon.getSerialDay());
        userWebtoonResponse.setStatus(webtoon.getStatus());
        userWebtoonResponse.setViewCount(webtoon.getViewCount());

        return userWebtoonResponse;

    }
}
