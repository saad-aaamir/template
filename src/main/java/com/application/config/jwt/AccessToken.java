package com.application.config.jwt;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@RedisHash(value = "AccessToken", timeToLive = 900) // 15 min expiry
public class AccessToken implements Serializable {

    @Id
    private String token;
    @Indexed
    private String email;
    private boolean revoked;
    private boolean expired;
    private Instant createdAt;
    private Instant lastUpdatedAt;

    public static AccessToken create(String token, String userEmail) {
        return AccessToken.builder()
                .token(token)
                .email(userEmail)
                .revoked(false)
                .expired(false)
                .createdAt(Instant.now())
                .lastUpdatedAt(Instant.now())
                .build();
    }
}
