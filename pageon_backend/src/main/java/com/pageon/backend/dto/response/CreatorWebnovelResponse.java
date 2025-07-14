package com.pageon.backend.dto.response;

import com.pageon.backend.common.enums.DayOfWeek;
import com.pageon.backend.common.enums.SeriesStatus;
import com.pageon.backend.entity.Webnovel;
import com.pageon.backend.entity.Webtoon;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreatorWebnovelResponse {
    private Long id;
    private String title;
    private String description;
    private List<CreatorKeywordResponse> keywords;
    private SeriesStatus status;
    private String cover;
    private DayOfWeek serialDay;

    public static CreatorWebnovelResponse fromEntity(Webnovel webnovel, List<CreatorKeywordResponse> keywords) {
        CreatorWebnovelResponse creatorContentResponse = new CreatorWebnovelResponse();
        creatorContentResponse.setId(webnovel.getId());
        creatorContentResponse.setTitle(webnovel.getTitle());
        creatorContentResponse.setDescription(webnovel.getDescription());
        creatorContentResponse.setKeywords(keywords);
        creatorContentResponse.setStatus(webnovel.getStatus());
        creatorContentResponse.setCover(webnovel.getCover());
        creatorContentResponse.setSerialDay(webnovel.getSerialDay());

        return creatorContentResponse;

    }


}
