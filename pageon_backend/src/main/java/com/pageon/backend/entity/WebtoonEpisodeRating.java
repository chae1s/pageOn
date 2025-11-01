package com.pageon.backend.entity;

import com.pageon.backend.common.enums.ContentType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicUpdate;

@Entity
@Getter
@Builder
@DynamicUpdate
@Table(name = "webtoon_episode_ratings")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class WebtoonEpisodeRating {
    // 평점 테이블

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "webtoonEpisode_id")
    private WebtoonEpisode webtoonEpisode;

    @Column(nullable = false)
    private Integer score;

    public void updateRating(Integer newScore) {
        this.score = newScore;
    }
}
