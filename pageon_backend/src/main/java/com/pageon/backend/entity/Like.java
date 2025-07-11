package com.pageon.backend.entity;

import com.pageon.backend.common.base.BaseTimeEntity;
import com.pageon.backend.common.enums.ContentType;
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
@Table(name = "likes")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Like extends BaseTimeEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    private ContentType contentType;     // 좋아요 작품이 웹툰인지 웹소설인지 표시
    private Integer contentId;      // 해당 작품의 id


}
