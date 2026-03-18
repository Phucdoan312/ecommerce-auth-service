package com.dvtp.authservice.application.service;

import com.dvtp.authservice.application.dto.ForgotPasswordCommand;
import com.dvtp.authservice.application.dto.ResetPasswordCommand;
import com.dvtp.authservice.domain.entity.User;
import com.dvtp.authservice.domain.exception.AppException;
import com.dvtp.authservice.domain.exception.ErrorCode;
import com.dvtp.authservice.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ForgotPasswordService {

    private final UserRepository userRepository;
    private final OtpService otpService;
    private final PasswordEncoder passwordEncoder;

    // 1. Xử lý yêu cầu gửi OTP
    @Transactional
    public void requestOtp(ForgotPasswordCommand command) {
        // Kiểm tra xem Email có tồn tại trong hệ thống không
        if (!userRepository.existsByEmail(command.email())) {
            throw new AppException(ErrorCode.USER_NOT_FOUND, "Email này chưa được đăng ký trong hệ thống.");
        }

        // Gọi OtpService để sinh mã và gửi Email
        otpService.generateAndSendOtp(command.email());
        log.info("📩 Đã yêu cầu gửi OTP quên mật khẩu cho: {}", command.email());
    }

    // 2. Xử lý đặt lại mật khẩu
    @Transactional
    public void resetPassword(ResetPasswordCommand command) {
        // Kiểm tra mật khẩu xác nhận
        if (!command.newPassword().equals(command.confirmPassword())) {
            throw new AppException(ErrorCode.PASSWORD_NOT_MATCH, "Mật khẩu xác nhận không khớp.");
        }

        // 1. Xác thực OTP (Nếu sai hoặc hết hạn, hàm này sẽ tự ném lỗi nổ tung ra ngoài)
        otpService.validateOtp(command.email(), command.otpCode());

        // 2. Tìm User
        // Ghi chú: Vì hàm findByUsernameOrEmail của ông tìm cả 2, mình truyền email vào cả 2 tham số cho chắc
        User user = userRepository.findByUsernameOrEmail(command.email(), command.email())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND, "Không tìm thấy User."));

        // 3. Đổi mật khẩu
        user.changePassword(passwordEncoder.encode(command.newPassword()));

        // 4. Lưu lại
        userRepository.save(user);
        log.info("🔓 User [{}] đã đặt lại mật khẩu thành công qua OTP.", command.email());
    }
}