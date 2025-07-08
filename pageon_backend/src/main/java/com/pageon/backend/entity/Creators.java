package com.pageon.backend.entity;

import com.pageon.backend.common.enums.CreatorType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicUpdate;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Builder
@DynamicUpdate
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Creators {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String penName;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private Users user;
    // 웹툰, 웹소설 작가 구분
    private CreatorType creatorType;

    @Builder.Default
    private Boolean isActive = true;

    @Builder.Default
    @OneToMany(mappedBy = "creator")
    private List<Webtoons> webtoons = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "creator")
    private List<Webnovels> webnovels = new ArrayList<>();

    public Creators(String penName, Users users, CreatorType creatorType, boolean isActive) {
        this.penName = penName;
        this.user = users;
        this.creatorType = creatorType;
        this.isActive = isActive;
    }

}
