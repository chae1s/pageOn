package com.pageon.backend.dto.response;

import com.pageon.backend.common.enums.Provider;
import lombok.Data;

@Data
public class JwtTokenResponse {
    private final Boolean isLogin;
    private final String accessToken;
    private final Provider provider;
}
