package com.delimce.aibroker.infrastructure.adapters;

import com.delimce.aibroker.domain.entities.User;
import com.delimce.aibroker.domain.enums.UserStatus;
import com.delimce.aibroker.domain.exceptions.security.JwtTokenException;

import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class JwtTokenAdapterTest {

    private JwtTokenAdapter jwtTokenAdapter;
    private User testUser;
    private static final String SECRET_KEY = "404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970";
    private static final long JWT_EXPIRATION = 86400000; // 1 day in milliseconds

    @BeforeEach
    void setUp() {
        jwtTokenAdapter = new JwtTokenAdapter();
        ReflectionTestUtils.setField(jwtTokenAdapter, "secretKey", SECRET_KEY);
        ReflectionTestUtils.setField(jwtTokenAdapter, "jwtExpiration", JWT_EXPIRATION);

        testUser = User.builder()
                .id(1L)
                .name("Test")
                .lastName("User")
                .email("test@example.com")
                .password("password")
                .status(UserStatus.ACTIVE)
                .build();
    }

    @Test
    void generateToken_WithUserDetails_ShouldReturnValidToken() {
        // when
        String token = jwtTokenAdapter.generateToken(testUser);

        // then
        assertNotNull(token);
        assertTrue(token.length() > 0);
    }

    @Test
    void generateToken_WithExtraClaims_ShouldIncludeClaimsInToken() throws JwtTokenException {
        // given
        Map<String, Object> extraClaims = new HashMap<>();
        extraClaims.put("role", "ADMIN");
        extraClaims.put("userId", 1L);

        // when
        String token = jwtTokenAdapter.generateToken(extraClaims, testUser);
        Claims claims = jwtTokenAdapter.extractAllClaims(token);

        // then
        assertNotNull(token);
        assertEquals("ADMIN", claims.get("role"));
        assertEquals(1, claims.get("userId"));
    }

    @Test
    void extractEmail_ShouldReturnCorrectEmail() throws JwtTokenException {
        // given
        String token = jwtTokenAdapter.generateToken(testUser);

        // when
        String email = jwtTokenAdapter.extractEmail(token);

        // then
        assertEquals(testUser.getEmail(), email);
    }

    @Test
    void extractExpiration_ShouldReturnFutureDate() throws JwtTokenException {
        // given
        String token = jwtTokenAdapter.generateToken(testUser);

        // when
        Date expirationDate = jwtTokenAdapter.extractExpiration(token);

        // then
        assertNotNull(expirationDate);
        assertTrue(expirationDate.after(new Date()));
    }

    @Test
    void extractClaim_WithCustomClaimResolver_ShouldReturnCorrectClaim() throws JwtTokenException {
        // given
        Map<String, Object> extraClaims = new HashMap<>();
        extraClaims.put("customClaim", "customValue");
        String token = jwtTokenAdapter.generateToken(extraClaims, testUser);
        Function<Claims, String> claimResolver = claims -> claims.get("customClaim", String.class);

        // when
        String claimValue = jwtTokenAdapter.extractClaim(token, claimResolver);

        // then
        assertEquals("customValue", claimValue);
    }

    @Test
    void extractAllClaims_ShouldReturnAllClaimsInToken() throws JwtTokenException {
        // given
        Map<String, Object> extraClaims = new HashMap<>();
        extraClaims.put("role", "USER");
        String token = jwtTokenAdapter.generateToken(extraClaims, testUser);

        // when
        Claims claims = jwtTokenAdapter.extractAllClaims(token);

        // then
        assertNotNull(claims);
        assertEquals(testUser.getEmail(), claims.getSubject());
        assertEquals("USER", claims.get("role"));
    }

    @Test
    void isTokenValid_WithValidTokenAndMatchingUser_ShouldReturnTrue() throws JwtTokenException {
        // given
        String token = jwtTokenAdapter.generateToken(testUser);

        // when
        boolean isValid = jwtTokenAdapter.isTokenValid(token, testUser);

        // then
        assertTrue(isValid);
    }

    @Test
    void isTokenValid_WithValidTokenButDifferentUser_ShouldReturnFalse() throws JwtTokenException {
        // given
        String token = jwtTokenAdapter.generateToken(testUser);
        User differentUser = User.builder()
                .email("different@example.com")
                .build();

        // when
        boolean isValid = jwtTokenAdapter.isTokenValid(token, differentUser);

        // then
        assertFalse(isValid);
    }

    @Test
    void isTokenValid_WithExpiredToken_ShouldReturnFalse() throws Exception, JwtTokenException {
        // given
        JwtTokenAdapter spyAdapter = spy(jwtTokenAdapter);
        String token = jwtTokenAdapter.generateToken(testUser);

        // Mock the extractExpiration method to return a past date
        Date pastDate = new Date(System.currentTimeMillis() - 1000);
        doReturn(pastDate).when(spyAdapter).extractExpiration(token);

        // when
        boolean isValid = spyAdapter.isTokenValid(token, testUser);

        // then
        assertFalse(isValid);
    }
}
