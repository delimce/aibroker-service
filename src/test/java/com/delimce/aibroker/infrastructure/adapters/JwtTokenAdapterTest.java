package com.delimce.aibroker.infrastructure.adapters;

import static org.junit.jupiter.api.Assertions.*;

import com.delimce.aibroker.domain.dto.values.UserToken;
import com.delimce.aibroker.domain.entities.User;
import com.delimce.aibroker.domain.enums.UserStatus;
import com.delimce.aibroker.domain.exceptions.security.JwtTokenException;
import io.jsonwebtoken.Claims;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

@SuppressWarnings({ "null", "deprecation" })
class JwtTokenAdapterTest {

    private JwtTokenAdapter jwtTokenAdapter;
    private User testUser;
    private static final String SECRET_KEY = "404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970";
    private static final long JWT_EXPIRATION = 86400000; // 1 day in milliseconds

    @BeforeEach
    void setUp() {
        jwtTokenAdapter = new JwtTokenAdapter();
        ReflectionTestUtils.setField(jwtTokenAdapter, "secretKey", SECRET_KEY);
        ReflectionTestUtils.setField(
                jwtTokenAdapter,
                "jwtExpiration",
                JWT_EXPIRATION);

        testUser = User.builder()
                .id(1L)
                .name("Test")
                .lastName("User")
                .email("test@example.com")
                .password("password")
                .tokenTs(new Date().getTime() / 1000)
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
    void generateToken_WithExtraClaims_ShouldIncludeClaimsInToken()
            throws JwtTokenException {
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
    void extractClaim_WithCustomClaimResolver_ShouldReturnCorrectClaim()
            throws JwtTokenException {
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
    void extractAllClaims_ShouldReturnAllClaimsInToken()
            throws JwtTokenException {
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
    void isTokenValid_WithValidTokenAndMatchingUser_ShouldReturnTrue()
            throws JwtTokenException {
        // given
        String token = jwtTokenAdapter.generateToken(testUser);

        // when
        boolean isValid = jwtTokenAdapter.isTokenValid(token, testUser);

        // then
        assertTrue(isValid);
    }

    @Test
    void isTokenValid_WithValidTokenButDifferentUser_ShouldReturnFalse()
            throws JwtTokenException {
        // given
        String token = jwtTokenAdapter.generateToken(testUser);
        User differentUser = User.builder()
                .email("different@example.com")
                .tokenTs(new Date().getTime() / 1000)
                .build();

        // when
        boolean isValid = jwtTokenAdapter.isTokenValid(token, differentUser);

        // then
        assertFalse(isValid);
    }

    @Test
    void isTokenValid_WithExpiredToken_ShouldThrowJwtTokenException() {
        // given - create adapter with very short expiration
        JwtTokenAdapter shortExpirationAdapter = new JwtTokenAdapter();
        ReflectionTestUtils.setField(
                shortExpirationAdapter,
                "secretKey",
                SECRET_KEY);
        ReflectionTestUtils.setField(
                shortExpirationAdapter,
                "jwtExpiration",
                -1000L); // negative = expired

        String expiredToken = shortExpirationAdapter.generateToken(testUser);

        // when & then - expired tokens throw exception when trying to extract claims
        assertThrows(JwtTokenException.class, () -> jwtTokenAdapter.isTokenValid(expiredToken, testUser));
    }

    @Test
    void isTokenValid_WithMismatchedTokenTs_ShouldReturnFalse()
            throws JwtTokenException {
        // given
        String token = jwtTokenAdapter.generateToken(testUser);

        // Change the tokenTs after token generation
        User userWithDifferentTokenTs = User.builder()
                .id(testUser.getId())
                .name(testUser.getName())
                .lastName(testUser.getLastName())
                .email(testUser.getEmail())
                .password(testUser.getPassword())
                .tokenTs(new Date().getTime() / 1000 + 1000) // different tokenTs
                .status(testUser.getStatus())
                .build();

        // when
        boolean isValid = jwtTokenAdapter.isTokenValid(
                token,
                userWithDifferentTokenTs);

        // then
        assertFalse(isValid);
    }

    // --- Invalid Token Tests ---

    @Test
    void extractEmail_WithNullToken_ShouldThrowJwtTokenException() {
        // when & then
        assertThrows(JwtTokenException.class, () -> jwtTokenAdapter.extractEmail(null));
    }

    @Test
    void extractEmail_WithEmptyToken_ShouldThrowJwtTokenException() {
        // when & then
        assertThrows(JwtTokenException.class, () -> jwtTokenAdapter.extractEmail(""));
    }

    @Test
    void extractEmail_WithMalformedToken_ShouldThrowJwtTokenException() {
        // given
        String malformedToken = "this.is.not.a.valid.jwt.token";

        // when & then
        assertThrows(JwtTokenException.class, () -> jwtTokenAdapter.extractEmail(malformedToken));
    }

    @Test
    void extractEmail_WithInvalidFormat_ShouldThrowJwtTokenException() {
        // given
        String invalidToken = "invalid-token-format";

        // when & then
        assertThrows(JwtTokenException.class, () -> jwtTokenAdapter.extractEmail(invalidToken));
    }

    @Test
    void extractExpiration_WithInvalidToken_ShouldThrowJwtTokenException() {
        // given
        String invalidToken = "eyJhbGciOiJIUzI1NiJ9.invalid.signature";

        // when & then
        assertThrows(JwtTokenException.class, () -> jwtTokenAdapter.extractExpiration(invalidToken));
    }

    @Test
    void extractExpiration_WithNullToken_ShouldThrowJwtTokenException() {
        // when & then
        assertThrows(JwtTokenException.class, () -> jwtTokenAdapter.extractExpiration(null));
    }

    @Test
    void extractIssuedAt_WithInvalidToken_ShouldThrowJwtTokenException() {
        // given
        String invalidToken = "completely.invalid.token";

        // when & then
        assertThrows(JwtTokenException.class, () -> jwtTokenAdapter.extractIssuedAt(invalidToken));
    }

    @Test
    void extractClaim_WithInvalidToken_ShouldThrowJwtTokenException() {
        // given
        String invalidToken = "not.a.token";
        Function<Claims, String> claimResolver = Claims::getSubject;

        // when & then
        assertThrows(JwtTokenException.class, () -> jwtTokenAdapter.extractClaim(invalidToken, claimResolver));
    }

    @Test
    void extractAllClaims_WithNullToken_ShouldThrowJwtTokenException() {
        // when & then
        assertThrows(JwtTokenException.class, () -> jwtTokenAdapter.extractAllClaims(null));
    }

    @Test
    void extractAllClaims_WithEmptyToken_ShouldThrowJwtTokenException() {
        // when & then
        assertThrows(JwtTokenException.class, () -> jwtTokenAdapter.extractAllClaims(""));
    }

    @Test
    void extractAllClaims_WithMalformedToken_ShouldThrowJwtTokenException() {
        // given
        String malformedToken = "header.payload.signature.extra";

        // when & then
        assertThrows(JwtTokenException.class, () -> jwtTokenAdapter.extractAllClaims(malformedToken));
    }

    @Test
    void extractAllClaims_WithTokenSignedWithDifferentKey_ShouldThrowJwtTokenException() {
        // given - create token with different secret key
        JwtTokenAdapter differentKeyAdapter = new JwtTokenAdapter();
        ReflectionTestUtils.setField(
                differentKeyAdapter,
                "secretKey",
                "3F4528482B4D6251655468576D5A7134743777217A25432A462D4A614E645267");
        ReflectionTestUtils.setField(
                differentKeyAdapter,
                "jwtExpiration",
                JWT_EXPIRATION);

        String tokenWithDifferentKey = differentKeyAdapter.generateToken(
                testUser);

        // when & then - should fail when verifying with original adapter
        assertThrows(JwtTokenException.class, () -> jwtTokenAdapter.extractAllClaims(tokenWithDifferentKey));
    }

    @Test
    void isTokenValid_WithNullToken_ShouldThrowJwtTokenException() {
        // when & then
        assertThrows(JwtTokenException.class, () -> jwtTokenAdapter.isTokenValid(null, testUser));
    }

    @Test
    void isTokenValid_WithEmptyToken_ShouldThrowJwtTokenException() {
        // when & then
        assertThrows(JwtTokenException.class, () -> jwtTokenAdapter.isTokenValid("", testUser));
    }

    @Test
    void isTokenValid_WithMalformedToken_ShouldThrowJwtTokenException() {
        // given
        String malformedToken = "this-is-not-a-jwt";

        // when & then
        assertThrows(JwtTokenException.class, () -> jwtTokenAdapter.isTokenValid(malformedToken, testUser));
    }

    @Test
    void isTokenValid_WithTamperedToken_ShouldThrowJwtTokenException() {
        // given
        String validToken = jwtTokenAdapter.generateToken(testUser);
        // Tamper with the token by changing a character in the signature
        String tamperedToken = validToken.substring(0, validToken.length() - 5) + "XXXXX";

        // when & then
        assertThrows(JwtTokenException.class, () -> jwtTokenAdapter.isTokenValid(tamperedToken, testUser));
    }

    @Test
    void extractClaim_WithNullClaimResolver_ShouldThrowJwtTokenException() {
        // given
        String token = jwtTokenAdapter.generateToken(testUser);

        // when & then
        assertThrows(JwtTokenException.class, () -> jwtTokenAdapter.extractClaim(token, null));
    }

    @Test
    void extractAllClaims_WithJustDotsToken_ShouldThrowJwtTokenException() {
        // given
        String invalidToken = "...";

        // when & then
        assertThrows(JwtTokenException.class, () -> jwtTokenAdapter.extractAllClaims(invalidToken));
    }

    @Test
    void extractEmail_WithTokenMissingParts_ShouldThrowJwtTokenException() {
        // given
        String incompleteToken = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0ZXN0QGV4YW1wbGUuY29tIn0";

        // when & then
        assertThrows(JwtTokenException.class, () -> jwtTokenAdapter.extractEmail(incompleteToken));
    }

    // --- generateUserToken Tests ---

    @Test
    void generateUserToken_ShouldReturnValidUserToken() throws JwtTokenException {
        // when
        UserToken userToken = jwtTokenAdapter.generateUserToken(testUser);

        // then
        assertNotNull(userToken);
        assertNotNull(userToken.token());
        assertNotNull(userToken.email());
        assertTrue(userToken.token().length() > 0);
    }

    @Test
    void generateUserToken_ShouldContainCorrectEmail() throws JwtTokenException {
        // when
        UserToken userToken = jwtTokenAdapter.generateUserToken(testUser);

        // then
        assertEquals(testUser.getEmail(), userToken.email());
    }

    @Test
    void generateUserToken_ShouldContainCorrectToken() throws JwtTokenException {
        // when
        UserToken userToken = jwtTokenAdapter.generateUserToken(testUser);
        String extractedEmail = jwtTokenAdapter.extractEmail(userToken.token());

        // then
        assertEquals(testUser.getEmail(), extractedEmail);
    }

    @Test
    void generateUserToken_ShouldHaveValidTimestamps() throws JwtTokenException {
        // given
        long beforeGeneration = System.currentTimeMillis() / 1000;

        // when
        UserToken userToken = jwtTokenAdapter.generateUserToken(testUser);

        // then
        long afterGeneration = System.currentTimeMillis() / 1000;

        // Verify issuedAt is reasonable (within 1 second)
        assertTrue(userToken.issuedAt() >= beforeGeneration);
        assertTrue(userToken.issuedAt() <= afterGeneration + 1);

        // Verify expiresAt is in the future
        assertTrue(userToken.expiresAt() > userToken.issuedAt());

        // Verify expiration duration is set correctly
        assertEquals(JWT_EXPIRATION, userToken.expirationTimeMs());
    }

    @Test
    void generateUserToken_ShouldNotBeExpired() throws JwtTokenException {
        // when
        UserToken userToken = jwtTokenAdapter.generateUserToken(testUser);

        // then
        assertFalse(userToken.isExpired());
    }

    @Test
    void generateUserToken_ShouldHavePositiveRemainingTime() throws JwtTokenException {
        // when
        UserToken userToken = jwtTokenAdapter.generateUserToken(testUser);

        // then
        assertTrue(userToken.getRemainingTimeMs() > 0);
        assertTrue(userToken.getRemainingTimeMs() <= JWT_EXPIRATION);
    }

    @Test
    void generateUserToken_ExpirationTimeMs_ShouldMatch() throws JwtTokenException {
        // when
        UserToken userToken = jwtTokenAdapter.generateUserToken(testUser);

        // then
        assertEquals(JWT_EXPIRATION, userToken.expirationTimeMs());
    }

    @Test
    void generateUserToken_WithMultipleCalls_ShouldGenerateDifferentTokens()
            throws JwtTokenException, InterruptedException {
        // when
        UserToken userToken1 = jwtTokenAdapter.generateUserToken(testUser);
        Thread.sleep(1100); // Sleep > 1 second to ensure different iat claim (JWT iat is in seconds)
        UserToken userToken2 = jwtTokenAdapter.generateUserToken(testUser);

        // then - issuedAt should be different
        assertNotEquals(userToken1.issuedAt(), userToken2.issuedAt());
    }

    @Test
    void generateUserToken_ShouldBeValidForIsTokenValid() throws JwtTokenException {
        // given
        UserToken userToken = jwtTokenAdapter.generateUserToken(testUser);

        // when
        boolean isValid = jwtTokenAdapter.isTokenValid(userToken.token(), testUser);

        // then
        assertTrue(isValid);
    }

    @Test
    void generateUserToken_WithDifferentUser_ShouldNotValidate() throws JwtTokenException {
        // given
        UserToken userToken = jwtTokenAdapter.generateUserToken(testUser);
        User differentUser = User.builder()
                .email("different@example.com")
                .tokenTs(testUser.getTokenTs())
                .build();

        // when & then
        assertFalse(jwtTokenAdapter.isTokenValid(userToken.token(), differentUser));
    }
}
