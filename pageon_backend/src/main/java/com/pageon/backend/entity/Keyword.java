package com.pageon.backend.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicUpdate;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

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

    @ManyToMany(mappedBy = "keywords")
    private Set<Webnovel> webnovels = new LinkedHashSet<>();

    @ManyToMany(mappedBy = "keywords")
    private Set<Webtoon> webtoons = new LinkedHashSet<>();

    public Keyword(Category category, String name) {
        this.category = category;
        this.name = name;
    }


}
