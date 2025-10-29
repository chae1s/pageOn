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
@Table(name = "webtoon_episodes")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class WebtoonEpisode extends BaseTimeEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "webtoon_id")
    private Webtoon webtoon;

    private Integer episodeNum;
    private String episodeTitle;

    @Builder.Default
    @OneToMany(mappedBy = "webtoonEpisode", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<WebtoonImage> images = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "webtoonEpisode", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<WebtoonEpisodeRating> webtoonEpisodeRatings = new ArrayList<>();
    // 대여 금액
    private Integer rentalPrice;
    // 구매 금액
    private Integer purchasePrice;

    @Builder.Default
    private Double averageRating = 0.0;

    @Builder.Default
    private Long ratingCount = 0L;

    public void addImage(WebtoonImage image) {
        this.images.add(image);
        image.addEpisode(this);
    }

}
