package com.pageon.backend.entity;

import com.pageon.backend.common.enums.Gender;
import com.pageon.backend.common.enums.IdentityProvider;
import com.pageon.backend.common.enums.OAuthProvider;
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

    @Builder.Default
    private Integer pointBalance = 0;

    // email, kakao, naver, google
    private OAuthProvider oAuthProvider;

    // 소셜 로그인 시 제공받는 id
    @Column(unique = true)
    private String providerId;

    // Soft Delete
    @Column(nullable = false)
    private Boolean isDeleted;

    @Column(nullable = false)
    private Boolean terms_agreed;

    // 본인인증 추가 정보
    private String name;
    @Column(unique = true)
    private String phoneNumber;
    private Gender gender;
    private String di;
    private Boolean isPhoneVerified;
    private IdentityProvider identityProvider;


    @OneToOne(mappedBy = "user", fetch = FetchType.LAZY)
    private Creators creators;

    @Builder.Default
    @OneToMany(mappedBy = "user")
    private List<Comments> comments = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "user")
    private List<Likes> likes = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "user")
    private List<PointTransactions> pointTransactions = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "user")
    private List<Ratings> ratings = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<UserRole> userRoles = new ArrayList<>();

    public void updatePassword(String newPassword) {
        this.password = newPassword;
    }

    public void updateNickname(String newNickname) {
        this.nickname = newNickname;
    }

    public void delete() {
        this.isDeleted = true;
    }

    public void deleteEmail(String deleteEmail) {
        this.email = deleteEmail;
    }

    public void deleteProviderId(String providerId) {
        this.providerId = providerId;
    }

    public void updateIdentityVerification(String name, String phoneNumber, LocalDate birthDate, Gender gender, String di, boolean isPhoneVerified, IdentityProvider identityProvider) {
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.birthDate = birthDate;
        this.gender = gender;
        this.di = di;
        this.isPhoneVerified = isPhoneVerified;
        this.identityProvider = identityProvider;
    }

    public Users(String email, String password, String nickname, LocalDate birthDate, Integer pointBalance, OAuthProvider provider, boolean isDeleted) {
        this.email = email;
        this.password = password;
        this.nickname = nickname;
        this.birthDate = birthDate;
        this.pointBalance = pointBalance;
        this.oAuthProvider = provider;
        this.isDeleted = isDeleted;
        this.userRoles = new ArrayList<>();
        this.terms_agreed = true;
    }


}
