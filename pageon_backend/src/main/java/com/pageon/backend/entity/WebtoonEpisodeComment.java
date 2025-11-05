package com.pageon.backend.entity;


import com.pageon.backend.common.base.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicUpdate;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Builder
@DynamicUpdate
@Table(name = "webtoon_episode_comments")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class WebtoonEpisodeComment extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "webtoon_episode_id")
    private WebtoonEpisode webtoonEpisode;

    @Column(nullable = false)
    private String text;

    @Builder.Default
    private Boolean isDeleted = false;

    private Boolean isSpoiler;


    @Builder.Default
    @OneToMany(mappedBy = "webtoonEpisodeComment", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<WebtoonEpisodeCommentLike> commentLikes = new ArrayList<>();
}
