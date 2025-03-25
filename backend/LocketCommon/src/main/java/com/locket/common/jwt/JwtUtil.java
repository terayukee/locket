package com.locket.common.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtUtil {

    private static final String SECRET_KEY = "locket-locket-locket-locket-locket-locket"; // 최소 256비트
    private static final long EXPIRATION_MS = 1000 * 60 * 60L; // 1시간

    private final Key key = Keys.hmacShaKeyFor(SECRET_KEY.getBytes());

    /**
     * JWT 토큰 생성
     */
    public String generateToken(Integer userId) {
        return Jwts.builder()
                .setSubject(userId.toString())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_MS))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * 토큰에서 userId 추출
     */
    public Integer extractUserId(String token) {
        return Integer.valueOf(parseToken(token).getBody().getSubject());
    }

    /**
     * 토큰 유효성 검증
     */
    public boolean validateToken(String token) {
        try {
            parseToken(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    /**
     * 내부 파싱 로직
     */
    private Jws<Claims> parseToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token);
    }
}
