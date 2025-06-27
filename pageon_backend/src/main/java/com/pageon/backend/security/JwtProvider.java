package com.pageon.backend.security;


import com.pageon.backend.entity.enums.RoleType;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.List;

@Slf4j
@Component
public class JwtProvider {

    private static final Long REFRESH_TOKEN_EXPIRES_IN = 180 * 24 * 60 * 60 * 1000L;
    private static final Long ACCESS_TOKEN_EXPIRES_IN = 30 * 60 * 1000L;
    private final Key refreshKey;
    private final Key accessKey;


    public JwtProvider(@Value("${jwt.secret.refresh}") String refreshSecretKey, @Value("${jwt.secret.access}") String accessSecretKey) {
        byte[] refreshKeyBytes = Decoders.BASE64.decode(refreshSecretKey);
        this.refreshKey = Keys.hmacShaKeyFor(refreshKeyBytes);

        byte[] accessKeyBytes = Decoders.BASE64.decode(accessSecretKey);
        this.accessKey = Keys.hmacShaKeyFor(accessKeyBytes);
    }

    /* Refresh Token 발급 */
    public String generateRefreshToken(Long id) {
        Date now = new Date();
        return Jwts.builder()
                .setSubject(String.valueOf(id))
                .claim("id", id)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + REFRESH_TOKEN_EXPIRES_IN))
                .signWith(refreshKey, SignatureAlgorithm.HS256)
                .compact();
    }


    /* Access Token 발급 */
    public String generateAccessToken(Long id, List<RoleType> roleTypes) {
        Date now = new Date();
        return Jwts.builder()
                .setSubject(String.valueOf(id))
                .claim("id", id)
                .claim("roles", roleTypes)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + ACCESS_TOKEN_EXPIRES_IN))
                .signWith(accessKey, SignatureAlgorithm.HS256)
                .compact();
    }

    public boolean validateToken(String token) {
        Jwts.parserBuilder()
                .setSigningKey(accessKey)
                .build()
                .parseClaimsJws(token);

        return true;
    }

    public String getUsername(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(accessKey)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .get("email", String.class);
    }

    public void sendTokens(HttpServletResponse response, String accessToken, String refreshToken) {

        response.setHeader("Authorization", "Bearer " + accessToken);

        Cookie cookie = new Cookie("refreshToken", refreshToken);
        cookie.setHttpOnly(true);
        cookie.setMaxAge(60 * 60 * 24 * 180);
        cookie.setSecure(true);
        cookie.setPath("/");

        response.addCookie(cookie);
    }



}
