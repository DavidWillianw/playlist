package com.playlist.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import javax.crypto.SecretKey;

@Component
public class JwtUtil {

    private static final String SECRET_KEY = 
        "2F4A6C8E1B3D5F7091A2C3E4F5678901ABCDEF1234567890FEDCBA0987654321";

    private final SecretKey SECRET = Keys.hmacShaKeyFor(
        SECRET_KEY.getBytes(StandardCharsets.UTF_8)
    );

    private final long EXPIRACAO = 5 * 60 * 1000;

    public String gerarToken(String login) {
        long now = System.currentTimeMillis();
        return Jwts.builder()
                .setSubject(login)
                .setIssuedAt(new Date(now))
                .setExpiration(new Date(now + EXPIRACAO))
                .signWith(SECRET, SignatureAlgorithm.HS256)
                .compact();
    }

    public String extrairLogin(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(SECRET)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }
}
