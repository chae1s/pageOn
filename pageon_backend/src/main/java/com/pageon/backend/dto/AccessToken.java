package com.pageon.backend.dto;

import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@RedisHash(value = "accessToken")
public class AccessToken {

    private Long id;

    private String accessToken;

    public AccessToken updateAccessToken(Long id, String accessToken) {
        this.id = id;
        this.accessToken = accessToken;

        return this;
    }
}
