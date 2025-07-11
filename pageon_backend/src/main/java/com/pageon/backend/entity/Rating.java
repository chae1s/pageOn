package com.pageon.backend.entity;

import com.pageon.backend.common.enums.ContentType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicUpdate;

@Entity
@Getter
@Builder
@DynamicUpdate
@Table(name = "ratings")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Rating {
    // 평점 테이블

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    private ContentType contentType;     // 댓글달린 작품이 웹툰인지 웹소설인지 표시
    private Integer contentId;

    private Integer score;
}
