package com.vimal.uber.utils;

import com.vimal.uber.security.CustomUserDetails;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
public class JwtUtil {

    @Value("${auth.ACCESS_TOKEN_SECRET}")
    private String accessTokenSecret;

    @Value("${auth.ACCESS_TOKEN_EXPIRATION}")
    private Long accessTokenExpiration;

    private Key getAccessTokenSigningKey() {
        return Keys.hmacShaKeyFor(accessTokenSecret.getBytes(StandardCharsets.UTF_8));
    }

    public String generateAccessToken(CustomUserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("type", "access");
        claims.put("username", userDetails.getUsername());
        claims.put("role", userDetails.getUser().getRole());
        return Jwts.builder()
                .addClaims(claims)
                .setSubject(userDetails.getId())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + accessTokenExpiration))
                .signWith(getAccessTokenSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    private Claims extractAllClaimsInAccessToken(String accessToken) {
        return Jwts.parserBuilder()
                .setSigningKey(getAccessTokenSigningKey())
                .build()
                .parseClaimsJws(accessToken)
                .getBody();
    }

    private <T> T extractClaimAccessToken(String token, Function<Claims, T> resolver) {
        final Claims claims = extractAllClaimsInAccessToken(token);
        return resolver.apply(claims);
    }

    public String getUsername(String accessToken) {
        return extractClaimAccessToken(accessToken, claims -> claims.get("username", String.class));
    }

    public String getIdAccessToken(String accessToken) {
        return extractClaimAccessToken(accessToken, Claims::getSubject);
    }

    private boolean isAccessTokenExpired(String token) {
        Date exp = extractClaimAccessToken(token, Claims::getExpiration);
        return exp.before(new Date());
    }

    private String getAccessTokenType(String accessToken) {
        return extractClaimAccessToken(accessToken, claims -> claims.get("type", String.class));
    }

    public boolean isValidAccessToken(String token, CustomUserDetails userDetails) {
        final String username = getUsername(token);
        return username.equals(userDetails.getUsername()) &&
                getAccessTokenType(token).equals("access") &&
                !isAccessTokenExpired(token);
    }
}