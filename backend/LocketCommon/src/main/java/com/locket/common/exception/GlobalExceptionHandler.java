package com.locket.common.exception;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;

@ControllerAdvice
public class GlobalExceptionHandler {

    // 공통 에러 응답 생성 메소드
    private ErrorResponse createErrorResponse(HttpStatus status, String error, String message) {
        return ErrorResponse.builder()
                .status(status.value())
                .error(error)
                .message(message)
                .timestamp(LocalDateTime.now())
                .build();
    }

    // 404 (NOT_FOUND) - ResourceNotFoundException과 모든 하위 예외를 처리
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(ResourceNotFoundException ex) {

        ErrorResponse errorResponse = createErrorResponse(
                HttpStatus.NOT_FOUND,
                "Not Found",
                ex.getMessage()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }

    // 401 (UNAUTHORIZED)
    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ErrorResponse> handleUnauthorized(UnauthorizedException ex) {
        ErrorResponse errorResponse = createErrorResponse(
                HttpStatus.UNAUTHORIZED,
                "Unauthorized",
                ex.getMessage()
        );
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
    }

    // 400 (BAD_REQUEST) - 유효성 검사 실패
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationExceptions(MethodArgumentNotValidException ex) {
        ErrorResponse errorResponse = createErrorResponse(
                HttpStatus.BAD_REQUEST,
                "Bad Request",
                "입력값 검증에 실패했습니다"
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    // 400 (BAD_REQUEST) - 일반적인 오류
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgument(IllegalArgumentException ex) {
        ErrorResponse errorResponse = createErrorResponse(
                HttpStatus.BAD_REQUEST,
                "Bad Request",
                ex.getMessage()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    // 403 (FORBIDDEN) - 접근 권한 없음
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDenied(AccessDeniedException ex) {
        ErrorResponse errorResponse = createErrorResponse(
                HttpStatus.FORBIDDEN,
                "Forbidden",
                ex.getMessage()
        );
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorResponse);
    }

    // 400 (BAD_REQUEST) - 유효하지 않은 요청
    @ExceptionHandler(InvalidRequestException.class)
    public ResponseEntity<ErrorResponse> handleInvalidRequest(InvalidRequestException ex) {
        ErrorResponse errorResponse = createErrorResponse(
                HttpStatus.BAD_REQUEST,
                "Bad Request",
                ex.getMessage()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    // 500 (INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneralException(Exception ex) {
        ErrorResponse errorResponse = createErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Internal Server Error",
                ex.getMessage()
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }

    @ExceptionHandler(KakaoApiException.class)
    public ResponseEntity<ErrorResponse> handleKakaoApiException(KakaoApiException ex) {
        HttpStatus status = ex.getStatus();
        ErrorResponse errorResponse = createErrorResponse(
                status,
                "Kakao API Error",
                ex.getMessage()
        );
        return ResponseEntity.status(status).body(errorResponse);
    }

    // 데이터 무결성 위반 예외
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> handleDataIntegrityViolation(DataIntegrityViolationException ex) {
        String message = ex.getMessage();
        String errorMessage = "데이터 무결성 위반이 발생했습니다.";

        if (message.contains("duplicate key") && message.contains("users_pkey")) {
            errorMessage = "이미 존재하는 사용자 ID입니다. 다시 시도해주세요.";
        } else if (message.contains("product_user_preferences")) {
            errorMessage = "이미 해당 상품에 대한 사용자 선호 설정이 존재합니다.";
        }

        ErrorResponse errorResponse = createErrorResponse(
                HttpStatus.BAD_REQUEST,
                "Bad Request",
                errorMessage
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }
}