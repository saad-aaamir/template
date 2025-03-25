package com.application.config.jwt;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

import java.io.Serializable;
import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@RedisHash(value = "RefreshToken", timeToLive = 86400) // 1 day expiry
public class RefreshToken implements Serializable {

    @Id
    private String token; // Use JWT itself as the key
    @Indexed
    private String email;
    private boolean revoked;
    private boolean expired;
    private Instant createdAt;
    private Instant lastUpdatedAt;

    public static RefreshToken create(String token, String userEmail) {
        return RefreshToken.builder()
                .token(token)
                .email(userEmail)
                .revoked(false)
                .expired(false)
                .createdAt(Instant.now())
                .lastUpdatedAt(Instant.now())
                .build();
    }
}
