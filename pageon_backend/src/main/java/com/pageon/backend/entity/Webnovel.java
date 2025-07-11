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
@Table(name = "webnovels")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Webnovel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    @Column(length = 1000)
    private  String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "creator_id")
    private Creator creator;

    private String cover;

    @ManyToMany
    @JoinTable(
            name = "webnovel_keyword",
            joinColumns = @JoinColumn(name = "webnovel_id"),
            inverseJoinColumns = @JoinColumn(name = "keyword_id")
    )
    private Set<Keyword> keywords = new HashSet<>();

    // 연재 요일
    private DayOfWeek publishDay;
    // 구매 금액
    private Integer purchasePrice;
    // 연재, 완결, 휴재
    private SeriesStatus status;

    private Long viewCount;

    public Webnovel(String title, String description, Set<Keyword> keywords, Creator creator, String cover, String publishDay, Integer purchasePrice, String status, Long viewCount) {
        this.title = title;
        this.description = description;
        this.keywords = keywords;
        this.creator = creator;
        this.cover = cover;
        this.publishDay = DayOfWeek.valueOf(publishDay);
        this.purchasePrice = purchasePrice;
        this.status = SeriesStatus.valueOf(status);
        this.viewCount = viewCount;
    }



}
