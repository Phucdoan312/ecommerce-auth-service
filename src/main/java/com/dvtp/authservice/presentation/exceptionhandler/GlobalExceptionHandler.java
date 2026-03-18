package com.dvtp.authservice.presentation.exceptionhandler;

import com.dvtp.authservice.domain.exception.AppException;
import com.dvtp.authservice.domain.exception.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(AppException.class)
    public ResponseEntity<ErrorResponse> handleAppException(AppException ex) {
        ErrorCode errorCode = ex.getErrorCode();
        log.error("❌ Business Exception [{}]: {} | Dev Message: {}",
                errorCode.getCode(), errorCode.getMessage(), ex.getDevMessage());

        return ResponseEntity.status(errorCode.getStatusCode()).body(new ErrorResponse(
                false, errorCode.getCode(), errorCode.getMessage(), ex.getDevMessage(), LocalDateTime.now()
        ));
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ErrorResponse> handleDomainState(IllegalStateException ex) {
        log.warn("⚠️ Domain Logic Violation: {}", ex.getMessage());

        if ("DOB_ALREADY_UPDATED".equals(ex.getMessage())) {
            ErrorCode errorCode = ErrorCode.DOB_ALREADY_SET;
            return ResponseEntity.status(errorCode.getStatusCode()).body(new ErrorResponse(
                    false, errorCode.getCode(), errorCode.getMessage(), "Nghiệp vụ: Ngày sinh chỉ được đổi 1 lần", LocalDateTime.now()
            ));
        }

        return handleGeneralException(ex);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex) {
        String detailedErrors = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining(" | "));

        String userMessage = ex.getBindingResult().getFieldErrors().get(0).getDefaultMessage();
        log.warn("⚠️ Validation Error: {}", detailedErrors);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse(
                false, "VALIDATION_ERROR", userMessage, detailedErrors, LocalDateTime.now()
        ));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneralException(Exception ex) {
        log.error("🔥 System Exception: ", ex);
        ErrorCode defaultError = ErrorCode.UNCATEGORIZED_EXCEPTION;

        return ResponseEntity.status(defaultError.getStatusCode()).body(new ErrorResponse(
                false, defaultError.getCode(), defaultError.getMessage(),
                ex.getClass().getSimpleName() + ": " + ex.getMessage(), LocalDateTime.now()
        ));
    }
}