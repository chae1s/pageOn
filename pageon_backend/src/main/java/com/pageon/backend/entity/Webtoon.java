package com.pageon.backend.entity;

import com.pageon.backend.common.enums.DayOfWeek;
import com.pageon.backend.common.enums.SeriesStatus;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.DynamicUpdate;

import java.util.*;

@Entity
@Getter
@SuperBuilder
@DynamicUpdate
@Table(name = "webtoons")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Webtoon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    @Column(length = 1000)
    private  String description;

    @ManyToMany
    @JoinTable(
            name = "webtoon_keyword",
            joinColumns = @JoinColumn(name = "webtoon_id"),
            inverseJoinColumns = @JoinColumn(name = "keyword_id")
    )
    private List<Keyword> keywords = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "creator_id")
    private Creator creator;

    private String cover;
    // 연재 요일
    private DayOfWeek serialDay;

    // 연재, 완결, 휴재
    @Builder.Default
    private SeriesStatus status = SeriesStatus.ONGOING;
    @Builder.Default
    private Long viewCount = 0L;

    public Webtoon(String title, String description, List<Keyword> keywords, Creator creator, String cover, String serialDay, String status, Long viewCount) {
        this.title = title;
        this.description = description;
        this.keywords = keywords;
        this.creator = creator;
        this.cover = cover;
        this.serialDay = DayOfWeek.valueOf(serialDay);
        this.status = SeriesStatus.valueOf(status);
        this.viewCount = viewCount;

    }

    public void updateCover(String s3Url) {
        this.cover = s3Url;
    }


}
