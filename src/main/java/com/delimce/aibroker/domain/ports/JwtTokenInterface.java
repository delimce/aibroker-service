package com.delimce.aibroker.domain.ports;

import io.jsonwebtoken.Claims;
import java.util.Date;
import java.util.Map;
import java.util.function.Function;
import com.delimce.aibroker.domain.entities.User;

public interface JwtTokenInterface {

    /**
     * Extract email from JWT token
     * 
     * @param token JWT token
     * @return extracted email
     */
    String extractEmail(String token);

    /**
     * Extract expiration date from JWT token
     * 
     * @param token JWT token
     * @return extracted expiration date
     */
    Date extractExpiration(String token);

    /**
     * Extract specific claim from JWT token
     * 
     * @param token          JWT token
     * @param claimsResolver function to extract specific claim
     * @return extracted claim
     */
    <T> T extractClaim(String token, Function<Claims, T> claimsResolver);

    /**
     * Extract all claims from JWT token
     * 
     * @param token JWT token
     * @return all claims
     */
    Claims extractAllClaims(String token);

    /**
     * Generate JWT token for user
     * 
     * @param userDetails user details
     * @return generated JWT token
     */
    String generateToken(User userDetails);

    /**
     * Generate JWT token with extra claims
     * 
     * @param extraClaims additional claims to include
     * @param userDetails user details
     * @return generated JWT token
     *         https://auth0.com/docs/secure/tokens/json-web-tokens/json-web-token-claims
     */
    String generateToken(Map<String, Object> extraClaims, User userDetails);

    /**
     * Validate JWT token
     * 
     * @param token       JWT token
     * @param userDetails user details
     * @return true if token is valid
     */
    boolean isTokenValid(String token, User userDetails);

}
