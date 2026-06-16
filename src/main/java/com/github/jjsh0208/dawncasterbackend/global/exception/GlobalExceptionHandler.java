package com.github.jjsh0208.dawncasterbackend.global.exception;


import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleIllegalArgumentException(IllegalArgumentException e) {
        log.warn("[잘못된 값 전달] {}", e.getMessage());
        return ResponseEntity.badRequest().body(e.getMessage());
    }

    // 그 외 예상치 못한 서버 에러
    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleGeneralException(Exception e) {
        log.error("[Server Error] {}", e.getMessage(), e);
        return ResponseEntity.internalServerError().body("서버 내부 오류가 발생했습니다.");
    }
}
