package com.pageon.backend.dto.response;

import com.pageon.backend.entity.User;
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

    public static UserInfoResponse fromEntity(User user) {
        UserInfoResponse userInfoResponse = new UserInfoResponse();
        userInfoResponse.setEmail(user.getEmail());
        userInfoResponse.setNickname(user.getNickname());
        userInfoResponse.setBirthDate(user.getBirthDate());
        userInfoResponse.setPointBalance(user.getPointBalance());
        userInfoResponse.setProvider(user.getOAuthProvider());
        userInfoResponse.setName(user.getName());
        userInfoResponse.setPhoneVerified(user.getIsPhoneVerified());
        return userInfoResponse;
    }
}
