package com.dvtp.authservice.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "otp_tokens")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OtpTokenJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String email; // Email nhận OTP

    @Column(nullable = false, length = 6)
    private String otpCode; // Mã OTP (vd: 482912)

    @Column(nullable = false)
    private LocalDateTime expirationTime; // Thời gian hết hạn (vd: 5 phút sau khi tạo)

    @Column(nullable = false)
    private boolean used; // Đánh dấu mã này đã được dùng hay chưa (chống xài lại 1 mã nhiều lần)

    // Hàm tiện ích để kiểm tra mã đã hết hạn chưa
    public boolean isExpired() {
        return LocalDateTime.now().isAfter(this.expirationTime);
    }
}