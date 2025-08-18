package com.uet.agent_simulation_api.utils;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.math.BigInteger;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

/**
 * This util is used to generate and validate jwt token.
 */
@Service
@Slf4j
public class JwtUtil {
    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.ttl}")
    private long jwtTtl; // jwt time to live

    @Value("${jwt.refresh_ttl}")
    private long jwtRefreshTtl; // jwt refresh time to live

    /*
     * This method is used to get signing key from jwt secret.
     *
     * @return SecretKey
     */
    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(jwtSecret.getBytes());
    }

    /*
     * This method is used to generate token.
     *
     * @param token String
     * @param ttl long
     *
     * @return T
     */
    private String generateToken(BigInteger id, long ttl, Map<String, Object> extraClaims) {
        return Jwts.builder()
                .subject(id.toString())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + ttl))
                .claims(extraClaims)
                .signWith(getSigningKey())
                .compact();
    }

    /*
     * This method is used to generate access token.
     *
     * @param id BigInteger
     *
     * @return String
     */
    public String generateAccessToken(BigInteger id) {
        return generateToken(id, jwtTtl, null);
    }

    /*
     * This method is used to generate refresh token.
     *
     * @param id BigInteger
     *
     * @return String
     */
    public String generateRefreshToken(BigInteger id) {
        return generateToken(id, jwtRefreshTtl, Map.of("random", UUID.randomUUID().toString()));
    }

    /*
     * This method is used to get user id from token.
     *
     * @param token String
     *
     * @return String
     */
    public String getUserIdFromToken(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

    /*
     * This method is used to validate token.
     *
     * @param token String
     *
     * @return boolean
     */
    public boolean isValidToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token);

            return true;
        } catch (SignatureException | MalformedJwtException | ExpiredJwtException | UnsupportedJwtException | IllegalArgumentException e) {
            log.info("Invalid token: {}", e.getMessage());
        }

        return false;
    }
}
