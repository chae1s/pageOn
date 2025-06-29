package com.pageon.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.redis.core.RedisHash;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@RedisHash(value = "token")
public class Token {


    private Long id;
    private String refreshToken;
    private String socialAccessToken;


    public Token updateRefreshToken(Long id, String refreshToken) {
        this.id = id;
        this.refreshToken = refreshToken;
        return this;
    }

    public Token updateSocialAccessToken(Long id, String socialAccessToken) {
        this.id = id;
        this.socialAccessToken = socialAccessToken;

        return this;
    }
}
