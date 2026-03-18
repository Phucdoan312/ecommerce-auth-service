package com.dvtp.authservice.presentation.exceptionhandler;

import java.time.LocalDateTime;

public record ErrorResponse(
        boolean success,
        String code,
        String message,
        String debugMessage,
        LocalDateTime timestamp
) {
}