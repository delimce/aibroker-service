package com.delimce.aibroker.domain.dto.values;

import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

public class UserTokenTest {

    @Test
    public void testUserTokenCreationWithValidParameters() {
        String token = "valid-jwt-token";
        String email = "user@example.com";
        long issuedAt = Instant.now().getEpochSecond();
        long expiresAt = issuedAt + 3600; // 1 hour from now
        long expirationTimeMs = 3600000L; // 1 hour in milliseconds

        UserToken userToken = new UserToken(token, email, issuedAt, expiresAt, expirationTimeMs);

        assertEquals(token, userToken.token());
        assertEquals(email, userToken.email());
        assertEquals(issuedAt, userToken.issuedAt());
        assertEquals(expiresAt, userToken.expiresAt());
        assertEquals(expirationTimeMs, userToken.expirationTimeMs());
    }

    @Test
    public void testUserTokenWithNullTokenThrowsException() {
        String email = "user@example.com";
        long issuedAt = Instant.now().getEpochSecond();
        long expiresAt = issuedAt + 3600;
        long expirationTimeMs = 3600000L;

        NullPointerException exception = assertThrows(NullPointerException.class, () -> {
            new UserToken(null, email, issuedAt, expiresAt, expirationTimeMs);
        });
        assertEquals("Token cannot be null", exception.getMessage());
    }

    @Test
    public void testUserTokenWithNullEmailThrowsException() {
        String token = "valid-jwt-token";
        long issuedAt = Instant.now().getEpochSecond();
        long expiresAt = issuedAt + 3600;
        long expirationTimeMs = 3600000L;

        NullPointerException exception = assertThrows(NullPointerException.class, () -> {
            new UserToken(token, null, issuedAt, expiresAt, expirationTimeMs);
        });
        assertEquals("Email cannot be null", exception.getMessage());
    }

    @Test
    public void testIsExpiredReturnsFalseForValidToken() {
        String token = "valid-jwt-token";
        String email = "user@example.com";
        long issuedAt = Instant.now().getEpochSecond();
        long expiresAt = issuedAt + 3600; // 1 hour from now
        long expirationTimeMs = 3600000L;

        UserToken userToken = new UserToken(token, email, issuedAt, expiresAt, expirationTimeMs);

        assertFalse(userToken.isExpired());
    }

    @Test
    public void testIsExpiredReturnsTrueForExpiredToken() {
        String token = "expired-jwt-token";
        String email = "user@example.com";
        long issuedAt = Instant.now().getEpochSecond() - 7200; // 2 hours ago
        long expiresAt = issuedAt + 3600; // Expired 1 hour ago
        long expirationTimeMs = 3600000L;

        UserToken userToken = new UserToken(token, email, issuedAt, expiresAt, expirationTimeMs);

        assertTrue(userToken.isExpired());
    }

    @Test
    public void testIsExpiredReturnsTrueWhenExpiresAtIsCurrentTime() {
        String token = "just-expired-token";
        String email = "user@example.com";
        long currentTime = Instant.now().getEpochSecond();
        long issuedAt = currentTime - 3600;
        long expiresAt = currentTime - 1; // Just expired
        long expirationTimeMs = 3600000L;

        UserToken userToken = new UserToken(token, email, issuedAt, expiresAt, expirationTimeMs);

        assertTrue(userToken.isExpired());
    }

    @Test
    public void testGetRemainingTimeMsReturnsPositiveValueForValidToken() {
        String token = "valid-jwt-token";
        String email = "user@example.com";
        long issuedAt = Instant.now().getEpochSecond();
        long expiresAt = issuedAt + 3600; // 1 hour from now
        long expirationTimeMs = 3600000L;

        UserToken userToken = new UserToken(token, email, issuedAt, expiresAt, expirationTimeMs);

        long remainingTime = userToken.getRemainingTimeMs();
        assertTrue(remainingTime > 0);
        assertTrue(remainingTime <= expirationTimeMs);
    }

    @Test
    public void testGetRemainingTimeMsReturnsZeroForExpiredToken() {
        String token = "expired-jwt-token";
        String email = "user@example.com";
        long issuedAt = Instant.now().getEpochSecond() - 7200; // 2 hours ago
        long expiresAt = issuedAt + 3600; // Expired 1 hour ago
        long expirationTimeMs = 3600000L;

        UserToken userToken = new UserToken(token, email, issuedAt, expiresAt, expirationTimeMs);

        assertEquals(0, userToken.getRemainingTimeMs());
    }

    @Test
    public void testGetRemainingTimeMsApproximatelyCorrect() {
        String token = "valid-jwt-token";
        String email = "user@example.com";
        long issuedAt = Instant.now().getEpochSecond();
        long expiresAt = issuedAt + 60; // 60 seconds from now
        long expirationTimeMs = 60000L;

        UserToken userToken = new UserToken(token, email, issuedAt, expiresAt, expirationTimeMs);

        long remainingTime = userToken.getRemainingTimeMs();
        // Should be approximately 60000ms, allowing for small timing differences
        assertTrue(remainingTime > 59000 && remainingTime <= 60000);
    }

    @Test
    public void testUserTokenEqualityWithSameValues() {
        String token = "valid-jwt-token";
        String email = "user@example.com";
        long issuedAt = 1000000000L;
        long expiresAt = 1000003600L;
        long expirationTimeMs = 3600000L;

        UserToken userToken1 = new UserToken(token, email, issuedAt, expiresAt, expirationTimeMs);
        UserToken userToken2 = new UserToken(token, email, issuedAt, expiresAt, expirationTimeMs);

        assertEquals(userToken1, userToken2);
        assertEquals(userToken1.hashCode(), userToken2.hashCode());
    }

    @Test
    public void testUserTokenInequalityWithDifferentTokens() {
        String email = "user@example.com";
        long issuedAt = 1000000000L;
        long expiresAt = 1000003600L;
        long expirationTimeMs = 3600000L;

        UserToken userToken1 = new UserToken("token1", email, issuedAt, expiresAt, expirationTimeMs);
        UserToken userToken2 = new UserToken("token2", email, issuedAt, expiresAt, expirationTimeMs);

        assertNotEquals(userToken1, userToken2);
    }

    @Test
    public void testUserTokenInequalityWithDifferentEmails() {
        String token = "valid-jwt-token";
        long issuedAt = 1000000000L;
        long expiresAt = 1000003600L;
        long expirationTimeMs = 3600000L;

        UserToken userToken1 = new UserToken(token, "user1@example.com", issuedAt, expiresAt, expirationTimeMs);
        UserToken userToken2 = new UserToken(token, "user2@example.com", issuedAt, expiresAt, expirationTimeMs);

        assertNotEquals(userToken1, userToken2);
    }
}
