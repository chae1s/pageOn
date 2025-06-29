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
    @Id
    private String providerId;
    @Indexed
    private String accessToken;
}
