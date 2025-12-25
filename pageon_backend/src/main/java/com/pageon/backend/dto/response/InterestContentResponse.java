package com.pageon.backend.dto.response;

import com.pageon.backend.common.enums.SerialDay;
import com.pageon.backend.common.enums.SeriesStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InterestContentResponse {
    private Long contentId;
    private String title;
    private String penName;
    private LocalDateTime episodeUpdatedAt;
    private String contentType;
    private String cover;
    private SerialDay serialDay;
    private SeriesStatus status;
}
