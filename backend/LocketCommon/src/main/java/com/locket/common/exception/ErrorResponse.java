package com.locket.common.exception;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
public class ErrorResponse {

    @Schema(type = "integer", description = "HTTP 상태 코드", example = "404")
    private int status;

    @Schema(description = "오류 유형", example = "NOT_FOUND")
    private String error;

    @Schema(description = "오류 메시지", example = "요청한 리소스를 찾을 수 없습니다.")
    private String message;

    @Schema(description = "오류 발생 시간", example = "2025-04-01T12:34:56")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime timestamp;

    public ErrorResponse(int status, String error, String message) {
        this.status = status;
        this.error = error;
        this.message = message;
        this.timestamp = LocalDateTime.now();
    }

    public static ErrorResponse of(int status, String error, String message) {
        return new ErrorResponse(status, error, message);
    }
}
