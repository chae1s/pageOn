package com.pageon.backend.entity;

import com.pageon.backend.common.enums.DayOfWeek;
import com.pageon.backend.common.enums.SeriesStatus;
import com.pageon.backend.dto.request.WebnovelUpdateRequest;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.DynamicUpdate;

import java.util.*;

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
    private List<Keyword> keywords = new ArrayList<>();

    // 연재 요일
    private DayOfWeek serialDay;
    // 연재, 완결, 휴재
    @Builder.Default
    private SeriesStatus status = SeriesStatus.ONGOING;
    @Builder.Default
    private Long viewCount = 0L;
    @Builder.Default
    private boolean isDeleted = false;

    public Webnovel(String title, String description, List<Keyword> keywords, Creator creator, String cover, String serialDay, String status, Long viewCount) {
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

    public void updateWebnovelInfo(WebnovelUpdateRequest request) {
        if (request.getTitle() != null) this.title = request.getTitle();
        if (request.getDescription() != null)this.description = request.getDescription();
        if (request.getSerialDay() != null) this.serialDay = DayOfWeek.valueOf(request.getSerialDay());
    }

    public void updateKeywords(List<Keyword> keywords) {
        if (keywords != null) {
            this.keywords.clear();
            this.keywords.addAll(keywords);
        }
    }

    public void updateStatus(String status) {
        if (status != null) this.status = SeriesStatus.valueOf(status);
    }

    public void updateIsDeleted(boolean isDeleted) {
        this.isDeleted = isDeleted;
    }

}
