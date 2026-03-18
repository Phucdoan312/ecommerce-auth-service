package com.dvtp.authservice.presentation.response;

import java.time.Instant;

public record ApiResponse<T>(
        boolean success,
        T data,
        String message,
        Instant timestamp
) {
    // Hàm tĩnh hỗ trợ trả về kết quả thành công CÓ data
    public static <T> ApiResponse<T> success(T data, String message) {
        return new ApiResponse<>(true, data, message, Instant.now());
    }

    // Hàm tĩnh hỗ trợ trả về kết quả thành công KHÔNG CÓ data (VD: API xóa user)
    public static <T> ApiResponse<T> success(String message) {
        return new ApiResponse<>(true, null, message, Instant.now());
    }
}