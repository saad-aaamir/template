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
@RedisHash(value = "AccessToken", timeToLive = 86400)
public class AccessToken implements Serializable {

    @Id
    private String token;
    @Indexed
    private String uuid;
    private boolean revoked;
    private boolean expired;
    private Instant createdAt;
    private Instant lastUpdatedAt;

    public static AccessToken create(String token, String uuid) {
        return AccessToken.builder()
                .token(token)
                .uuid(uuid)
                .revoked(false)
                .expired(false)
                .createdAt(Instant.now())
                .lastUpdatedAt(Instant.now())
                .build();
    }
}
