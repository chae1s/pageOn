package com.pageon.backend.dto;

import lombok.Data;

@Data
public class UserUpdateRequest {

    private String password;

    private String nickname;

}
