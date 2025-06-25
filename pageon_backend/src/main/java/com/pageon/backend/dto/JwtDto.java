package com.pageon.backend.dto;

import lombok.Data;

@Data
public class JwtDto {
    private final Boolean isLogin;
    private final String accessToken;
}
