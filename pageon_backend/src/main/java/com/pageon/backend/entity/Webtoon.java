package com.pageon.backend.entity;

import com.pageon.backend.common.enums.DayOfWeek;
import com.pageon.backend.common.enums.SeriesStatus;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.DynamicUpdate;

import java.util.HashSet;
import java.util.Set;

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
    private Set<Keyword> keywords = new HashSet<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "creator_id")
    private Creator creator;

    private String cover;
    // 연재 요일
    private DayOfWeek publishDay;
    // 대여 금액
    private Integer rentalPrice;
    // 구매 금액
    private Integer purchasePrice;
    // 연재, 완결, 휴재
    private SeriesStatus status;
    private Long viewCount;

    public Webtoon(String title, String description, Set<Keyword> keywords, Creator creator, String cover, String publishDay, Integer rentalPrice, Integer purchasePrice, String status, Long viewCount) {
        this.title = title;
        this.description = description;
        this.keywords = keywords;
        this.creator = creator;
        this.cover = cover;
        this.publishDay = DayOfWeek.valueOf(publishDay);
        this.rentalPrice = rentalPrice;
        this.purchasePrice = purchasePrice;
        this.status = SeriesStatus.valueOf(status);
        this.viewCount = viewCount;

    }


}
