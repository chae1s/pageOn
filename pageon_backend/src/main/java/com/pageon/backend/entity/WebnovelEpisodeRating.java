package com.pageon.backend.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicUpdate;

@Entity
@Getter
@Builder
@DynamicUpdate
@Table(name = "webnovel_episode_ratings")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class WebnovelEpisodeRating {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "webnovelEpisode_id")
    private WebnovelEpisode webnovelEpisode;

    @Column(nullable = false)
    private Integer score;

    public void updateRating(Integer newScore) {
        this.score = newScore;
    }

}
