package com.dvtp.authservice.domain.exception;

import lombok.Getter;

@Getter
public class AppException extends RuntimeException {
    private final ErrorCode errorCode;
    private final String devMessage;

    public AppException(ErrorCode errorCode, String devMessage) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
        this.devMessage = devMessage;
    }

    // Thêm cái này để bắt được nguyên nhân gốc (Root Cause)
    public AppException(ErrorCode errorCode, String devMessage, Throwable cause) {
        super(errorCode.getMessage(), cause);
        this.errorCode = errorCode;
        this.devMessage = devMessage;
    }
}