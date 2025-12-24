package com.pageon.backend.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicUpdate;

import java.util.*;

@Entity
@Getter
@Builder
@DynamicUpdate
@Table(name = "keywords")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Keyword {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;
    private String name;

    @Builder.Default
    @ManyToMany(mappedBy = "keywords")
    private List<Content> contents = new ArrayList<>();


    public Keyword(Category category, String name) {
        this.category = category;
        this.name = name;
    }


}