package com.delimce.aibroker.infrastructure.adapters;

import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

import com.delimce.aibroker.utils.TestHandler;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class JwtTokenAdapterTest extends TestHandler {

    @InjectMocks
    private JwtTokenAdapter jwtTokenAdapter;

    private UserDetails userDetails;
    private final String username = "testuser";
    private final String secretKey = "ZFg5Mzk3ZGZnZGZnZHNmZzQ1amhndnNkZmdoc2RmZzlkZnN5dXNkZ3JlZ3dnZ2RmZGZkZmRnZGZnZGY=";
    private final long expirationTime = 3600000; // 1 hour

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        userDetails = new User(username, "password", new ArrayList<>());
        ReflectionTestUtils.setField(jwtTokenAdapter, "secretKey", secretKey);
        ReflectionTestUtils.setField(jwtTokenAdapter, "jwtExpiration", expirationTime);
    }

    @Test
    void generateToken_shouldCreateValidToken() {
        // When
        String token = jwtTokenAdapter.generateToken(userDetails);

        // Then
        assertNotNull(token);
        assertEquals(username, jwtTokenAdapter.extractUsername(token));
        assertTrue(jwtTokenAdapter.isTokenValid(token, userDetails));
    }

    @Test
    void generateTokenWithClaims_shouldCreateValidTokenWithClaims() {
        // Given
        var fakeRole = faker().name().username();
        Map<String, Object> extraClaims = new HashMap<>();
        extraClaims.put("role", fakeRole);

        // When
        String token = jwtTokenAdapter.generateToken(extraClaims, userDetails);

        // Then
        assertNotNull(token);
        assertEquals(username, jwtTokenAdapter.extractUsername(token));
        assertEquals(fakeRole, jwtTokenAdapter.extractClaim(token, claims -> claims.get("role")));
        assertTrue(jwtTokenAdapter.isTokenValid(token, userDetails));
    }

    @Test
    void extractUsername_shouldReturnCorrectUsername() {
        // Given
        String token = jwtTokenAdapter.generateToken(userDetails);

        // When
        String extractedUsername = jwtTokenAdapter.extractUsername(token);

        // Then
        assertEquals(username, extractedUsername);
    }

    @Test
    void extractExpiration_shouldReturnCorrectExpirationDate() {
        // Given
        String token = jwtTokenAdapter.generateToken(userDetails);

        // When
        Date expirationDate = jwtTokenAdapter.extractExpiration(token);

        // Then
        assertNotNull(expirationDate);
        long expectedExpirationTime = System.currentTimeMillis() + expirationTime;
        long actualExpirationTime = expirationDate.getTime();
        // Allow a small delta for test execution time
        assertTrue(Math.abs(expectedExpirationTime - actualExpirationTime) < 1000);
    }

    @Test
    void extractAllClaims_shouldReturnAllClaims() {
        // Given
        Map<String, Object> extraClaims = new HashMap<>();

        var fakeRole = faker().name().username();
        var fakeUserId = 1234;

        extraClaims.put("role", fakeRole);
        extraClaims.put("userId", fakeUserId);
        String token = jwtTokenAdapter.generateToken(extraClaims, userDetails);

        // When
        Claims claims = jwtTokenAdapter.extractAllClaims(token);

        // Then
        assertNotNull(claims);
        assertEquals(username, claims.getSubject());
        assertEquals(fakeRole, claims.get("role"));
        assertEquals(fakeUserId, claims.get("userId"));
    }

    @Test
    void isTokenValid_shouldReturnTrueForValidToken() {
        // Given
        String token = jwtTokenAdapter.generateToken(userDetails);

        // When
        boolean isValid = jwtTokenAdapter.isTokenValid(token, userDetails);

        // Then
        assertTrue(isValid);
    }

    @Test
    void isTokenValid_shouldReturnFalseForDifferentUser() {
        // Given
        String token = jwtTokenAdapter.generateToken(userDetails);
        UserDetails differentUser = new User(faker().name().username(), "password", new ArrayList<>());

        // When
        boolean isValid = jwtTokenAdapter.isTokenValid(token, differentUser);

        // Then
        assertFalse(isValid);
    }
}