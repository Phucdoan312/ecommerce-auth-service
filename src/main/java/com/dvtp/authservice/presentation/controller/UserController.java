package com.dvtp.authservice.presentation.controller;

import com.dvtp.authservice.application.dto.ChangePasswordCommand;
import com.dvtp.authservice.application.dto.UpdateProfileCommand;
import com.dvtp.authservice.application.dto.UserResponse;
import com.dvtp.authservice.application.usecase.ChangePasswordUseCase;
import com.dvtp.authservice.application.usecase.GetUserProfileUseCase;
import com.dvtp.authservice.application.usecase.UpdateProfileUseCase;
import com.dvtp.authservice.presentation.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "2. User Management API", description = "Quản lý thông tin cá nhân của người dùng")
public class UserController {

    private final UpdateProfileUseCase updateProfileUseCase;
    private final ChangePasswordUseCase changePasswordUseCase;
    private final GetUserProfileUseCase getUserProfileUseCase;

    @Operation(
            summary = "Cập nhật hồ sơ cá nhân",
            description = "Cập nhật số điện thoại và ngày sinh. Trả về thông tin user mới nhất. Lưu ý: Ngày sinh chỉ đổi 1 lần.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @PutMapping("/profile")
    public ResponseEntity<ApiResponse<UserResponse>> updateProfile(
            @AuthenticationPrincipal UUID userId,
            @Valid @RequestBody UpdateProfileCommand command) {

        // Service giờ sẽ trả về UserResponse (chứa data mới nhất)
        UserResponse updatedUser = updateProfileUseCase.updateProfile(userId, command);

        return ResponseEntity.ok(ApiResponse.success(updatedUser, "Cập nhật hồ sơ thành công!"));
    }

    @Operation(
            summary = "Đổi mật khẩu",
            description = "Yêu cầu mật khẩu cũ chính xác. Thành công chỉ trả về thông báo, không kèm data.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @PostMapping("/change-password")
    public ResponseEntity<ApiResponse<Void>> changePassword(
            @AuthenticationPrincipal UUID userId,
            @Valid @RequestBody ChangePasswordCommand command) {

        // Service chạy void, nếu có lỗi nó tự ném Exception ra GlobalExceptionHandler
        changePasswordUseCase.changePassword(userId, command);

        // Trả về data = null, chỉ báo success = true
        return ResponseEntity.ok(ApiResponse.success(null, "Đổi mật khẩu thành công!"));
    }

    @Operation(summary = "Lấy thông tin cá nhân (Profile)", description = "Yêu cầu gửi kèm Access Token trên Header")
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserResponse>> getMyProfile() {
        UserResponse data = getUserProfileUseCase.getMyProfile();
        return ResponseEntity.ok(ApiResponse.success(data, "Lấy thông tin thành công"));
    }

}