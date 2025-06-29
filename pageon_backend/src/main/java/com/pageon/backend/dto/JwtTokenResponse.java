package com.pageon.backend.dto;

import com.pageon.backend.entity.enums.Provider;
import lombok.Data;

@Data
public class JwtTokenResponse {
    private final Boolean isLogin;
    private final String accessToken;
    private final Provider provider;
}
