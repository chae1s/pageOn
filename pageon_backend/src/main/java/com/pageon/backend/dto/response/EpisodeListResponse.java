package com.pageon.backend.dto.response;

import com.pageon.backend.common.base.EpisodeBase;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EpisodeListResponse {
    private Long id;
    private Integer episodeNum;
    private String episodeTitle;
    private LocalDateTime createdAt;
    private Integer purchasePrice;
    private Integer rentalPrice;
    private EpisodePurchaseResponse episodePurchase;


    public static EpisodeListResponse fromEntity(EpisodeBase episode, EpisodePurchaseResponse episodePurchase) {
        return EpisodeListResponse.builder()
                .id(episode.getId())
                .episodeNum(episode.getEpisodeNum())
                .episodeTitle(episode.getEpisodeTitle())
                .createdAt(episode.getCreatedAt())
                .purchasePrice(episode.getPurchasePrice())
                .rentalPrice(episode.getRentalPrice())
                .episodePurchase(episodePurchase)
                .build();
    }
}
