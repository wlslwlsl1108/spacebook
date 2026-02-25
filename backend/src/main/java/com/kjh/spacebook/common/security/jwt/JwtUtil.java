package com.kjh.spacebook.common.security.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Date;

@Component
public class JwtUtil {
    private final SecretKey key;
    private final JwtParser parser;
    private final Duration accessTokenExpiration;
    private final Duration refreshTokenExpiration;

    public JwtUtil(JwtProperties jwtProperties) {
        this.key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtProperties.secret()));
        this.parser = Jwts.parser()
                .clockSkewSeconds(60)
                .verifyWith(key)
                .build();
        this.accessTokenExpiration = Duration.ofMinutes(jwtProperties.accessTokenExpireMinutes());
        this.refreshTokenExpiration = Duration.ofDays(jwtProperties.refreshTokenExpireDays());
    }

    public String createAccessToken(Long userId, String role) {
        Instant now = Instant.now();

        return Jwts.builder()
                .subject(String.valueOf(userId))
                .claim("role", role)
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plus(accessTokenExpiration)))
                .signWith(key)
                .compact();
    }

    public String createRefreshToken(Long userId) {
        Instant now = Instant.now();

        return Jwts.builder()
                .subject(String.valueOf(userId))
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plus(refreshTokenExpiration)))
                .signWith(key)
                .compact();
    }

    public Claims validateAndGetClaims(String token) {
        return parser.parseSignedClaims(token).getPayload();
    }

    public LocalDateTime calculateRefreshExpiry() {
        return LocalDateTime.now().plus(refreshTokenExpiration);
    }
}
