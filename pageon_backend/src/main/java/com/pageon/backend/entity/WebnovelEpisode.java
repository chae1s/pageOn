package com.pageon.backend.entity;

import com.pageon.backend.common.base.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.DynamicUpdate;

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

}
