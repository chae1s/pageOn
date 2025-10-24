package com.pageon.backend.dto.response;

import com.pageon.backend.common.enums.SerialDay;
import com.pageon.backend.common.enums.SeriesStatus;
import com.pageon.backend.entity.Webnovel;
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
    private String cover;
    private String author;
    private List<UserKeywordResponse> keywords;
    private SerialDay serialDay;
    private SeriesStatus status;
    private Double rating;
    private Integer ratingCount;
    private Long viewCount;
    private String contentType;
    private Boolean isInterested;

    // 에피소드 담기
    private List<EpisodeListResponse> episodes;

    public static UserWebnovelResponse fromEntity(Webnovel webnovel, List<UserKeywordResponse> keywords, List<EpisodeListResponse> episodes, Boolean isInterested) {
        UserWebnovelResponse userWebnovelResponse = new UserWebnovelResponse();
        userWebnovelResponse.setId(webnovel.getId());
        userWebnovelResponse.setTitle(webnovel.getTitle());
        userWebnovelResponse.setDescription(webnovel.getDescription());
        userWebnovelResponse.setCover(webnovel.getCover());
        userWebnovelResponse.setAuthor(webnovel.getCreator().getPenName());
        userWebnovelResponse.setKeywords(keywords);
        userWebnovelResponse.setSerialDay(webnovel.getSerialDay());
        userWebnovelResponse.setStatus(webnovel.getStatus());
        userWebnovelResponse.setRating(4.9231);
        userWebnovelResponse.setRatingCount(125039);
        userWebnovelResponse.setViewCount(webnovel.getViewCount());
        userWebnovelResponse.setContentType("webnovels");
        userWebnovelResponse.setEpisodes(episodes);
        userWebnovelResponse.setIsInterested(isInterested);

        return userWebnovelResponse;

    }
}
