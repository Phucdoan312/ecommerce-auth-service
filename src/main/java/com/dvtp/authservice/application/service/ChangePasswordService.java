package com.dvtp.authservice.application.service;

import com.dvtp.authservice.application.dto.ChangePasswordCommand;
import com.dvtp.authservice.application.usecase.ChangePasswordUseCase;
import com.dvtp.authservice.domain.entity.User;
import com.dvtp.authservice.domain.exception.AppException;
import com.dvtp.authservice.domain.exception.ErrorCode;
import com.dvtp.authservice.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChangePasswordService implements ChangePasswordUseCase {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void changePassword(UUID userId, ChangePasswordCommand command) {
        // 1. Kiểm tra mật khẩu mới và xác nhận có khớp nhau không
        if (!command.newPassword().equals(command.confirmPassword())) {
            throw new AppException(ErrorCode.PASSWORD_NOT_MATCH,
                    "Change Pass Failed: New Password và Confirm Password không trùng nhau.");
        }

        // 2. Truy xuất User từ Database
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND,
                        "Change Pass Failed: Không tìm thấy User ID [" + userId + "]."));

        // 3. Kiểm tra mật khẩu cũ (Mật khẩu hiện tại) có đúng không
        if (!passwordEncoder.matches(command.oldPassword(), user.getPassword())) {
            throw new AppException(ErrorCode.OLD_PASSWORD_INVALID,
                    "Change Pass Failed: Mật khẩu cũ nhập vào không chính xác.");
        }

        // 4. KIỂM TRA NGHIỆP VỤ: Mật khẩu mới không được trùng với mật khẩu hiện tại
        if (passwordEncoder.matches(command.newPassword(), user.getPassword())) {
            throw new AppException(ErrorCode.VALIDATION_ERROR,
                    "Change Pass Failed: Mật khẩu mới không được giống với mật khẩu đang sử dụng.");
        }

        // 5. Mã hóa mật khẩu mới và cập nhật vào Domain Entity
        user.changePassword(passwordEncoder.encode(command.newPassword()));

        // 6. Lưu thông tin thay đổi xuống Database
        userRepository.save(user);

        // 7. Ghi log Audit bảo mật
        log.info("🛡️ [AUDIT] User [{}] đã thay đổi mật khẩu thành công.", userId);
    }
}