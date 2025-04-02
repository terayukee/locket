package com.locket.common.exception;

public class JwtAuthException extends RuntimeException {
    public JwtAuthException() {
        super("JWT 인증 오류가 발생했습니다.");
    }

    public JwtAuthException(String message) {
        super(message);
    }

    public JwtAuthException(String message, Throwable cause) {
        super(message, cause);
    }
}
