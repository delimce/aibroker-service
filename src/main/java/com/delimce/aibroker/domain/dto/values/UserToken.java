package com.delimce.aibroker.domain.dto.values;

import java.time.Instant;
import java.util.Objects;

public record UserToken(
        String token,
        String email,
        long issuedAt, // Unix timestamp in seconds
        long expiresAt, // Unix timestamp in seconds
        long expirationTimeMs) { // Expiration duration in milliseconds

    public UserToken {
        Objects.requireNonNull(token, "Token cannot be null");
        Objects.requireNonNull(email, "Email cannot be null");
    }

    public boolean isExpired() {
        return Instant.now().getEpochSecond() > expiresAt;
    }

    public long getRemainingTimeMs() {
        long remaining = (expiresAt * 1000) - System.currentTimeMillis();
        return Math.max(remaining, 0);
    }
}