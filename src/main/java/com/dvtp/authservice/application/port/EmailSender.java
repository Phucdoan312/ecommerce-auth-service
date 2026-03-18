package com.dvtp.authservice.application.port;

public interface EmailSender {
    void sendOtpEmail(String toEmail, String otpCode);
}