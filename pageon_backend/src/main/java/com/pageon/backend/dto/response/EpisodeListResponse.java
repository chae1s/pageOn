package com.pageon.backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EpisodeListResponse {
    private Long id;
    private Integer episodeNum;
    private String episodeTitle;
    private LocalDateTime createdAt;
    private Integer purchasePrice;
    private Integer rentalPrice;

    public static EpisodeListResponse fromEntity(Long episodeId, Integer episodeNum, String episodeTitle, LocalDateTime createdAt, Integer purchasePrice, Integer rentalPrice) {
        return new EpisodeListResponse(episodeId, episodeNum, episodeTitle, createdAt, purchasePrice, rentalPrice);
    }
}
