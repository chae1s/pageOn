package com.pageon.backend.entity;

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
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String category;   // 키워드 상위 분류

    @Builder.Default
    @OneToMany(mappedBy = "category")
    private List<Keywords> keywords = new ArrayList<>();

    public Category(String category) {
        this.category = category;
    }
}
