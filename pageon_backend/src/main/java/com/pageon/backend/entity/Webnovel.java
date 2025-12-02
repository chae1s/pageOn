package com.pageon.backend.entity;

import com.pageon.backend.common.base.BaseTimeEntity;
import com.pageon.backend.common.enums.SerialDay;
import com.pageon.backend.common.enums.SeriesStatus;
import com.pageon.backend.dto.request.ContentUpdateRequest;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.DynamicUpdate;

import java.time.DayOfWeek;
import java.util.*;

@Entity
@Getter
@SuperBuilder
@DynamicUpdate
@Table(name = "webnovels")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Webnovel extends BaseTimeEntity {

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

    @Builder.Default
    private List<Keyword> keywords = new ArrayList<>();

    // 연재 요일
    @Enumerated(EnumType.STRING)
    private SerialDay serialDay;
    // 연재, 완결, 휴재
    @Builder.Default
    @Enumerated(EnumType.STRING)
    private SeriesStatus status = SeriesStatus.ONGOING;
    @Builder.Default
    private Long viewCount = 0L;
    @Builder.Default
    private boolean deleted = false;

    @Builder.Default
    private Double totalAverageRating = 0.0;
    @Builder.Default
    private Long totalRatingCount = 0L;

    public Webnovel(Long id, String title, Creator creator, SerialDay serialDay, Long viewCount) {
        this.id = id;
        this.title = title;
        this.creator = creator;
        this.serialDay = serialDay;
        this.viewCount = viewCount;
    }

    public void updateCover(String s3Url) {
        this.cover = s3Url;
    }

    public void updateWebnovelInfo(ContentUpdateRequest request) {
        if (request.getTitle() != null) this.title = request.getTitle();
        if (request.getDescription() != null)this.description = request.getDescription();
        if (request.getSerialDay() != null) this.serialDay = SerialDay.valueOf(request.getSerialDay());
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

    public void updateDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public void addRating(Integer score) {
        double totalScore = this.totalAverageRating * this.totalRatingCount;
        this.totalRatingCount++;
        this.totalAverageRating = (totalScore + score) / this.totalRatingCount;
    }

    public void updateRating(Integer oldScore, Integer newScore) {
        if (this.totalRatingCount == 0) return;

        this.totalAverageRating = this.totalAverageRating + ((double) (newScore - oldScore) / this.totalRatingCount);
    }

}