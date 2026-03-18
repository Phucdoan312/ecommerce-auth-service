package com.dvtp.authservice.domain.exception;

import lombok.Getter;

@Getter
public enum ErrorCode {
    // --- LỖI LIÊN QUAN ĐẾN TÀI KHOẢN (AUTH) ---
    USER_ALREADY_EXISTS("AUTH_001", 400, "Tên đăng nhập đã tồn tại, vui lòng chọn tên khác."),
    EMAIL_ALREADY_IN_USE("AUTH_002", 400, "Email đã được sử dụng."),
    INVALID_CREDENTIALS("AUTH_003", 401, "Sai tên đăng nhập hoặc mật khẩu."),
    USER_NOT_FOUND("AUTH_004", 404, "Không tìm thấy thông tin tài khoản."),
    PASSWORD_NOT_MATCH("AUTH_005", 400, "Mật khẩu xác nhận không khớp với mật khẩu mới."),
    ACCOUNT_DISABLED("AUTH_006", 403, "Tài khoản của bạn đã bị vô hiệu hóa. Vui lòng liên hệ Admin."),
    OLD_PASSWORD_INVALID("AUTH_007", 400, "Mật khẩu cũ không chính xác."),
    DOB_ALREADY_SET("AUTH_008", 400, "Ngày sinh chỉ được cập nhật duy nhất một lần."),

    // --- LỖI LIÊN QUAN ĐẾN BẢO MẬT & PHÂN QUYỀN (SECURITY) ---
    UNAUTHENTICATED("SEC_001", 401, "Bạn chưa đăng nhập hoặc Token đã hết hạn."),
    UNAUTHORIZED("SEC_002", 403, "Bạn không có quyền thực hiện thao tác này."),

    // --- LỖI HỆ THỐNG (SYSTEM) ---
    VALIDATION_ERROR("SYS_002", 400, "Dữ liệu đầu vào không hợp lệ."),
    ROLE_NOT_FOUND("SYS_001", 500, "Hệ thống đang bảo trì, không thể tạo tài khoản lúc này."),
    UNCATEGORIZED_EXCEPTION("SYS_999", 500, "Đã có lỗi bất ngờ xảy ra, vui lòng thử lại sau.");

    private final String code;
    private final int statusCode;
    private final String message;

    ErrorCode(String code, int statusCode, String message) {
        this.code = code;
        this.statusCode = statusCode;
        this.message = message;
    }
}