package com.chanseok.api.config.security.token;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.function.Function;

@Component
@EnableConfigurationProperties(TokenProperties.class)
public class TokenProvider {
    private final Key key;
    private final TokenProperties properties;

    public TokenProvider(TokenProperties properties) {
        byte[] keyBytes = Decoders.BASE64.decode(properties.getSecretKey());
        this.key = Keys.hmacShaKeyFor(keyBytes);
        this.properties = properties;
    }

    public String generateToken(String email) {
        return buildToken(email, properties.getExpirationTime());
    }

    private String buildToken(String email, Long expirationTime) {
        return Jwts.builder()
                    .setSubject(email)
                    .setIssuedAt(new Date(System.currentTimeMillis()))
                    .setExpiration(new Date(System.currentTimeMillis() + expirationTime))
                    .signWith(key, SignatureAlgorithm.HS512)
                .compact();
    }

    public String extractEmail(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public boolean isValid(String token, String email) {
        String findEmail = extractEmail(token);
        return findEmail.equals(email) && !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    private <T> T extractClaim(String token, Function<Claims, T> function) {
        Claims claims = extractAllClaims(token);
        return function.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}