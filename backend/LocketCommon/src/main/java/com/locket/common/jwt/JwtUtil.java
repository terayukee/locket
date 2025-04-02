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
    private long accessTokenValidity;
    @Value("${jwt.refresh-exp}")
    private long refreshTokenValidity;

    public enum TokenStatus {
        VALID,
        EXPIRED,
        INVALID_SIGNATURE,
        MALFORMED,
        UNSUPPORTED,
        INVALID
    }

    private Key getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(this.secret);
        return Keys.hmacShaKeyFor(keyBytes);
    }

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

    public Long getUserIdFromToken(String token) {
        Claims claims = getClaimsFromToken(token);
        return claims.get("userId", Long.class);
    }

    public boolean isAccessToken(String token) {
        return "access_token".equals(getClaimsFromToken(token).get("type"));
    }

    public Date getExpirationFromToken(String token) {
        return getClaimsFromToken(token).getExpiration();
    }

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

    public boolean validateToken(String token) {
        return validateTokenWithStatus(token) == TokenStatus.VALID;
    }

    public void validateRefreshToken(String token) throws JwtAuthException {
        try {
            TokenStatus status = validateTokenWithStatus(token);

            if (status == TokenStatus.EXPIRED) {
                throw new JwtAuthException("만료된 리프레시 토큰입니다. 다시 로그인해주세요.");
            } else if (status != TokenStatus.VALID) {
                throw new JwtAuthException("유효하지 않은 리프레시 토큰입니다: " + status);
            }

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
