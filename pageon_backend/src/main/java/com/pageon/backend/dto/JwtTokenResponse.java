package com.pageon.backend.dto;

import lombok.Data;

@Data
public class JwtTokenResponse {
    private final Boolean isLogin;
    private final String accessToken;
}
