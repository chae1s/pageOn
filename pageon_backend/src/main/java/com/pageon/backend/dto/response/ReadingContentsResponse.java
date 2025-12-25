package com.pageon.backend.dto.response;

import com.pageon.backend.common.enums.SerialDay;
import com.pageon.backend.common.enums.SeriesStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReadingContentsResponse {
    private Long contentId;
    private String title;
    private String penName;
    private String cover;
    private LocalDateTime episodeUpdatedAt;
    private LocalDateTime lastReadAt;
    private Long lastReadEpisodeId;
    private String contentType;
    private SerialDay serialDay;
    private SeriesStatus status;

}
