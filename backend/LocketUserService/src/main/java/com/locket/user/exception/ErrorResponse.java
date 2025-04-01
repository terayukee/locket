package com.locket.user.exception;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
public class ErrorResponse {

    @Schema(type = "integer", description = "HTTP 상태 코드")
    private int status;

    @Schema(description = "오류 유형")
    private String error;

    @Schema(description = "오류 메시지")
    private String message;

    @Schema(description = "오류 발생 시간")
    private LocalDateTime timestamp;

    public ErrorResponse(int status, String error, String message) {
        this.status = status;
        this.error = error;
        this.message = message;
        this.timestamp = LocalDateTime.now();
    }

}