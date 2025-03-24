package com.locket.user.exception;

public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException() {
        super("해당 데이터를 찾을 수 없습니다.");
    }

    public ResourceNotFoundException(String message) {
        super(message);
    }
}