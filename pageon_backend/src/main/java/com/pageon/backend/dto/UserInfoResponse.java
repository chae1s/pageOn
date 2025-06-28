package com.pageon.backend.dto;

import com.pageon.backend.entity.Users;
import lombok.Data;

import java.time.LocalDate;

@Data
public class UserInfoResponse {
    private String email;
    private String nickname;
    private LocalDate birthDate;
    private Integer pointBalance;

    public static UserInfoResponse fromEntity(Users users) {
        UserInfoResponse userInfoResponse = new UserInfoResponse();
        userInfoResponse.setEmail(users.getEmail());
        userInfoResponse.setNickname(users.getNickname());
        userInfoResponse.setBirthDate(users.getBirthDate());
        userInfoResponse.setPointBalance(users.getPointBalance());

        return userInfoResponse;
    }
}
