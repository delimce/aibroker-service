package com.delimce.aibroker.infrastructure.adapters;

import com.delimce.aibroker.domain.dto.values.UserToken;
import com.delimce.aibroker.domain.entities.User;
import com.delimce.aibroker.domain.exceptions.security.JwtTokenException;
import com.delimce.aibroker.domain.ports.JwtTokenInterface;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class JwtTokenAdapter implements JwtTokenInterface {

    @Value("${spring.security.jwt.secret-key}")
    private String secretKey;

    @Value("${spring.security.jwt.expiration}")
    private long jwtExpiration;

    @Override
    public String extractEmail(String token) throws JwtTokenException {
        return extractClaim(token, Claims::getSubject);
    }

    @Override
    public Date extractExpiration(String token) throws JwtTokenException {
        return extractClaim(token, Claims::getExpiration);
    }

    @Override
    public long extractIssuedAt(String token) throws JwtTokenException {
        Date issuedAt = extractClaim(token, Claims::getIssuedAt);
        return issuedAt.getTime() / 1000;
    }

    @Override
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver)
            throws JwtTokenException {
        try {
            final Claims claims = extractAllClaims(token);
            return claimsResolver.apply(claims);
        } catch (Exception e) {
            throw new JwtTokenException();
        }
    }

    @Override
    public Claims extractAllClaims(String token) throws JwtTokenException {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(getSignInKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (Exception e) {
            throw new JwtTokenException();
        }
    }

    @Override
    @Deprecated
    public String generateToken(User userDetails) {
        return generateToken(new HashMap<>(), userDetails);
    }

    @Override
    public String generateToken(Map<String, Object> extraClaims, User userDetails) {
        return Jwts.builder()
                .setClaims(extraClaims)
                .setSubject(userDetails.getEmail())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpiration))
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    @Override
    public boolean isTokenValid(String token, User userDetails)
            throws JwtTokenException {
        final String email = extractEmail(token);
        final long issuedAt = extractIssuedAt(token);
        return (email.equals(userDetails.getEmail()) &&
                !isTokenExpired(token) &&
                issuedAt == userDetails.getTokenTs());
    }

    private boolean isTokenExpired(String token) throws JwtTokenException {
        return extractExpiration(token).before(new Date());
    }

    private Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public UserToken generateUserToken(User userDetails) throws JwtTokenException {
        String token = generateToken(userDetails);
        long issuedAt = extractIssuedAt(token);
        long expiresAt = extractExpiration(token).getTime() / 1000;
        long expirationTimeMs = jwtExpiration;

        return new UserToken(
                token,
                userDetails.getEmail(),
                issuedAt,
                expiresAt,
                expirationTimeMs);
    }
}
