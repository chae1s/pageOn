package com.pageon.backend.entity;

import com.pageon.backend.common.base.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.DynamicUpdate;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@SuperBuilder
@DynamicUpdate
@Table(name = "webnovel_episodes")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class WebnovelEpisode extends BaseTimeEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "webnovel_id")
    private Webnovel webnovel;

    private Integer episodeNum;
    private String episodeTitle;

    @Column(columnDefinition = "TEXT")
    private String content;

    @Builder.Default
    @OneToMany(mappedBy = "webnovelEpisode", cascade = CascadeType.ALL,  orphanRemoval = true)
    private List<WebnovelEpisodeRating> webnovelEpisodeRatings = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "webnovelEpisode", cascade = CascadeType.ALL,  orphanRemoval = true)
    private List<WebnovelEpisodeComment> webnovelEpisodeComments = new ArrayList<>();

    // 구매 금액
    private Integer purchasePrice;

    @Builder.Default
    private Double averageRating = 0.0;

    @Builder.Default
    private Long ratingCount = 0L;

    @Builder.Default
    private Boolean isDeleted = false;

    public void addRating(Integer score) {
        double totalScore = this.averageRating * this.ratingCount;
        this.ratingCount++;
        this.averageRating = (totalScore + score) / this.ratingCount;
    }

    public void updateRating(Integer oldScore, Integer newScore) {
        if (this.ratingCount == 0) return;

        this.averageRating = this.averageRating + ((double) (newScore - oldScore) / this.ratingCount);
    }

}
