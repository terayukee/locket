package com.locket.common.jwt;

import com.locket.common.exception.JwtAuthException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.SignatureException;

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

    // 토큰 상태
    public enum TokenStatus {
        VALID,           // 유효한 토큰
        EXPIRED,         // 만료된 토큰
        INVALID_SIGNATURE, // 서명이 유효하지 않음
        MALFORMED,       // 잘못된 형식
        UNSUPPORTED,     // 지원되지 않는 토큰
        INVALID          // 기타 오류
    }

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

    // 개선된 토큰 검증 메서드 - 상태값 반환
    public TokenStatus validateTokenWithStatus(String token) {
        try {
            getClaimsFromToken(token);
            return TokenStatus.VALID;
        } catch (ExpiredJwtException e) {
            return TokenStatus.EXPIRED;
        } catch (SignatureException e) {
            return TokenStatus.INVALID_SIGNATURE;
        } catch (MalformedJwtException e) {
            return TokenStatus.MALFORMED;
        } catch (UnsupportedJwtException e) {
            return TokenStatus.UNSUPPORTED;
        } catch (JwtException e) {
            return TokenStatus.INVALID;
        }
    }

    // 기존 메서드와의 호환성 유지
    public boolean validateToken(String token) {
        return validateTokenWithStatus(token) == TokenStatus.VALID;
    }

    // 리프레시 토큰 검증
    public void validateRefreshToken(String token) throws JwtAuthException {
        try {
            // 토큰 유효성 검증
            TokenStatus status = validateTokenWithStatus(token);

            if (status == TokenStatus.EXPIRED) {
                throw new JwtAuthException("만료된 리프레시 토큰입니다. 다시 로그인해주세요.");
            } else if (status != TokenStatus.VALID) {
                throw new JwtAuthException("유효하지 않은 리프레시 토큰입니다: " + status);
            }

            // 토큰 타입 검증
            Claims claims = getClaimsFromToken(token);
            String tokenType = claims.get("type", String.class);

            if (!"refresh_token".equals(tokenType)) {
                throw new JwtAuthException("액세스 토큰이 전달되었습니다. 리프레시 토큰을 전달해주세요.");
            }

        } catch (ExpiredJwtException e) {
            throw new JwtAuthException("만료된 리프레시 토큰입니다. 다시 로그인해주세요.");
        } catch (JwtAuthException e) {
            throw e;
        } catch (JwtException e) {
            throw new JwtAuthException("유효하지 않은 토큰 형식입니다: " + e.getMessage());
        }
    }
}