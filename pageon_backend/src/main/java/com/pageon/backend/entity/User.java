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
@Table(name = "users")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class User {

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
    private Creator creator;

    @Builder.Default
    @OneToMany(mappedBy = "user")
    private List<Comment> comments = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "user")
    private List<Like> likes = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "user")
    private List<PointTransaction> pointTransactions = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "user")
    private List<Rating> ratings = new ArrayList<>();

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

    public User(String email, String password, String nickname, Integer pointBalance, OAuthProvider oAuthProvider, boolean isDeleted) {
        this.email = email;
        this.password = password;
        this.nickname = nickname;
        this.pointBalance = pointBalance;
        this.oAuthProvider = oAuthProvider;
        this.isDeleted = isDeleted;
        this.userRoles = new ArrayList<>();
        this.terms_agreed = true;
    }


}
