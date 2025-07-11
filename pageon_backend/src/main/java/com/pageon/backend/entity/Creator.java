package com.pageon.backend.entity;

import com.pageon.backend.common.enums.ContentType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicUpdate;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Builder
@DynamicUpdate
@Table(name = "creators")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Creator {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String penName;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
    // 웹툰, 웹소설 작가 구분
    private ContentType creatorType;

    @Builder.Default
    private Boolean isActive = true;
    // AI 콘텐츠 약관 동의 여부
    private Boolean agreedToAiPolicy;
    private LocalDateTime aiPolicyAgreedAt;

    @Builder.Default
    @OneToMany(mappedBy = "creator")
    private List<Webtoon> webtoons = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "creator")
    private List<Webnovel> webnovels = new ArrayList<>();

    public Creator(String penName, User users, ContentType creatorType, boolean isActive) {
        this.penName = penName;
        this.user = users;
        this.creatorType = creatorType;
        this.isActive = isActive;
    }

}
