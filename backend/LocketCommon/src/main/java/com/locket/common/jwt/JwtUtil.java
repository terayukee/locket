package com.locket.common.jwt;

import com.locket.common.exception.JwtAuthException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.io.Decoders;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.access-exp}")
    private long accessTokenValidity; // 86400000 (24시간)

    @Value("${jwt.refresh-exp}")
    private long refreshTokenValidity; // 604800000 (7일)

    private Key getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(this.secret);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    // 액세스 토큰 생성
    public String createAccessToken(Long userId, String nickname) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        claims.put("nickname", nickname);
        claims.put("type", "access_token");

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + accessTokenValidity))
                .signWith(SignatureAlgorithm.HS256, getSigningKey())
                .compact();
    }

    // 리프레시 토큰 생성
    public String createRefreshToken(Long userId) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        claims.put("type", "refresh_token");

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + refreshTokenValidity))
                .signWith(SignatureAlgorithm.HS256, getSigningKey())
                .compact();
    }


    public Claims getClaimsFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(this.getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }


    // 토큰에서 사용자 ID 추출 (클레임 기반)
    public Long getUserIdFromToken(String token) {
        Claims claims = getClaimsFromToken(token);
        return claims.get("userId", Long.class);
    }

    // 토큰 타입 체크 메서드(AccessToken인지 확인)
    public boolean isAccessToken(String token) {
        return "access_token".equals(getClaimsFromToken(token).get("type"));
    }

    // 토큰의 만료시간 가져오는 메서드
    public Date getExpirationFromToken(String token) {
        return getClaimsFromToken(token).getExpiration();
    }

    // 토큰 유효성 검증
    public boolean validateToken(String token) {
        try {
            getClaimsFromToken(token);
            return true;
        } catch (ExpiredJwtException e) {
            // 만료된 토큰에 대한 별도 처리 가능
            return false;
        } catch (JwtException e) {
            return false;
        }
    }

    // 리프레시 토큰 검증
    public void validateRefreshToken(String token) throws JwtAuthException {
        try {

            if (!validateToken(token)) {
                throw new JwtAuthException("유효하지 않은 리프레시 토큰입니다.");
            }

            // 리프레시 토큰 타입 확인
            if (isAccessToken(token)) {
                throw new JwtAuthException("액세스 토큰이 전달되었습니다. 리프레시 토큰을 전달해주세요.");
            }

        } catch (ExpiredJwtException e) {
            throw new JwtAuthException("만료된 리프레시 토큰입니다. 다시 로그인해주세요.");
        } catch (JwtException e) {
            throw new JwtAuthException("유효하지 않은 토큰 형식입니다: " + e.getMessage());
        }
    }
}