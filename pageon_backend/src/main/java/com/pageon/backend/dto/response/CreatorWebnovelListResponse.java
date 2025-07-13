package com.pageon.backend.dto.response;

import com.pageon.backend.common.enums.DayOfWeek;
import com.pageon.backend.common.enums.SeriesStatus;
import com.pageon.backend.entity.Webnovel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreatorWebnovelListResponse {
    private String title;
    private SeriesStatus status;
    private String cover;
    private DayOfWeek serialDay;

    public static CreatorWebnovelListResponse fromEntity(Webnovel webnovel) {
        CreatorWebnovelListResponse creatorWebnovelListResponse = new CreatorWebnovelListResponse();
        creatorWebnovelListResponse.setTitle(webnovel.getTitle());
        creatorWebnovelListResponse.setStatus(webnovel.getStatus());
        creatorWebnovelListResponse.setCover(webnovel.getCover());
        creatorWebnovelListResponse.setSerialDay(webnovel.getSerialDay());

        return creatorWebnovelListResponse;

    }
}
