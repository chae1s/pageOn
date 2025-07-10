package com.pageon.backend.dto.response;

import com.pageon.backend.entity.Users;
import com.pageon.backend.common.enums.OAuthProvider;
import lombok.Data;

import java.time.LocalDate;

@Data
public class UserInfoResponse {
    private String email;
    private String nickname;
    private LocalDate birthDate;
    private Integer pointBalance;
    private OAuthProvider provider;
    private String name;
    private boolean isPhoneVerified;

    public static UserInfoResponse fromEntity(Users users) {
        UserInfoResponse userInfoResponse = new UserInfoResponse();
        userInfoResponse.setEmail(users.getEmail());
        userInfoResponse.setNickname(users.getNickname());
        userInfoResponse.setBirthDate(users.getBirthDate());
        userInfoResponse.setPointBalance(users.getPointBalance());
        userInfoResponse.setProvider(users.getOAuthProvider());
        userInfoResponse.setName(users.getName());
        userInfoResponse.setPhoneVerified(users.getIsPhoneVerified());
        return userInfoResponse;
    }
}
