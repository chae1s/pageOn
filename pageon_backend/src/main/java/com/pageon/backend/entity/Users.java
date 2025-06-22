package com.pageon.backend.entity;

import com.pageon.backend.entity.enums.Provider;
import com.pageon.backend.entity.enums.Role;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.DynamicUpdate;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@SuperBuilder
@DynamicUpdate
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Users {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(
            unique = true,
            nullable = false
    )
    private String email;

    private String password;

    @Column(
            unique = true,
            nullable = false
    )
    private String nickname;
    private LocalDate birthDate;

    private Integer pointBalance = 0;

    private Role role;

    // email, kakao, naver, google
    @Column(unique = true)
    private Provider provider;

    // 소셜 로그인 시 제공받는 id
    @Column(unique = true)
    private Long providerId;

    @OneToMany(mappedBy = "user")
    private List<Webtoons> webtoons = new ArrayList<>();

    @OneToMany(mappedBy = "user")
    private List<Comments> comments = new ArrayList<>();

    @OneToMany(mappedBy = "user")
    private List<Likes> likes = new ArrayList<>();

    @OneToMany(mappedBy = "user")
    private List<PointTransactions> pointTransactions = new ArrayList<>();

    @OneToMany(mappedBy = "user")
    private List<Ratings> ratings = new ArrayList<>();

}
