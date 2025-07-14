package com.pageon.backend.dto.response;

import com.pageon.backend.common.enums.DayOfWeek;
import com.pageon.backend.common.enums.SeriesStatus;
import com.pageon.backend.entity.Creator;
import com.pageon.backend.entity.Keyword;
import com.pageon.backend.entity.Webnovel;
import com.pageon.backend.entity.Webtoon;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserWebnovelResponse {
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


    public static UserWebnovelResponse fromEntity(Webnovel webnovel, List<UserKeywordResponse> keywords) {
        UserWebnovelResponse userWebnovelResponse = new UserWebnovelResponse();
        userWebnovelResponse.setId(webnovel.getId());
        userWebnovelResponse.setTitle(webnovel.getTitle());
        userWebnovelResponse.setDescription(webnovel.getDescription());
        userWebnovelResponse.setCoverUrl(webnovel.getCover());
        userWebnovelResponse.setPenName(webnovel.getCreator().getPenName());
        userWebnovelResponse.setKeywords(keywords);
        userWebnovelResponse.setSerialDay(webnovel.getSerialDay());
        userWebnovelResponse.setStatus(webnovel.getStatus());
        userWebnovelResponse.setViewCount(webnovel.getViewCount());

        return userWebnovelResponse;

    }
}
