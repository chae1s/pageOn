package com.pageon.backend.dto.response;

import com.pageon.backend.common.enums.DayOfWeek;
import com.pageon.backend.common.enums.SeriesStatus;
import com.pageon.backend.entity.Keyword;
import com.pageon.backend.entity.Webnovel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreatorWebnovelResponse {
    private String title;
    private String description;
    private List<CreatorKeywordResponse> keywords;
    private SeriesStatus status;
    private String cover;
    private DayOfWeek serialDay;

    public static CreatorWebnovelResponse fromEntity(Webnovel webnovel, List<CreatorKeywordResponse> keywords) {
        CreatorWebnovelResponse creatorWebnovelResponse = new CreatorWebnovelResponse();
        creatorWebnovelResponse.setTitle(webnovel.getTitle());
        creatorWebnovelResponse.setDescription(webnovel.getDescription());
        creatorWebnovelResponse.setKeywords(keywords);
        creatorWebnovelResponse.setStatus(webnovel.getStatus());
        creatorWebnovelResponse.setCover(webnovel.getCover());
        creatorWebnovelResponse.setSerialDay(webnovel.getSerialDay());

        return creatorWebnovelResponse;

    }

}
