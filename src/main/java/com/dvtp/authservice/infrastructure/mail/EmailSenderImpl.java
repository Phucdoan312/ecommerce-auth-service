package com.dvtp.authservice.infrastructure.mail;

import com.dvtp.authservice.application.port.EmailSender;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailSenderImpl implements EmailSender {

    private final JavaMailSender javaMailSender;

    @Value("${spring.mail.username}")
    private String senderEmail;

    @Override
    public void sendOtpEmail(String toEmail, String otpCode) {
        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(senderEmail);
            helper.setTo(toEmail);
            helper.setSubject("🔑 Mã Xác Thực (OTP) - E-Commerce");

            // Thay %s bằng {{OTP_CODE}} để tránh đụng độ với dấu % của CSS
            String htmlTemplate = """
                    <div style="font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; background-color: #F0F4F9; padding: 40px 20px; margin: 0;">
                        <div style="max-width: 500px; margin: 0 auto; background-color: #ffffff; border-radius: 12px; overflow: hidden; box-shadow: 0 4px 10px rgba(0,0,0,0.05);">
                            
                            <div style="background: linear-gradient(90deg, #2b5fed 0%, #4f46e5 100%); padding: 25px; text-align: center;">
                                <h2 style="margin: 0; font-size: 24px; color: #ffffff; letter-spacing: 1px;">E-Commerce</h2>
                            </div>
                            
                            <div style="padding: 30px; color: #333333; line-height: 1.6;">
                                <p style="font-size: 16px; margin-top: 0;">Chào bạn,</p>
                                <p style="font-size: 16px;">Bạn vừa yêu cầu mã xác thực (OTP) từ hệ thống. Dưới đây là mã bảo mật của bạn:</p>
                                
                                <div style="text-align: center; margin: 35px 0;">
                                    <span style="display: inline-block; font-size: 36px; font-weight: bold; color: #2563EB; background-color: #EFF6FF; padding: 15px 35px; border-radius: 8px; letter-spacing: 8px; border: 1px solid #BFDBFE;">
                                        {{OTP_CODE}}
                                    </span>
                                </div>
                                
                                <p style="font-size: 15px; color: #555555;">
                                    Mã này sẽ hết hạn trong <strong style="color: #E63946;">2 phút</strong>.<br/>
                                    Vui lòng không chia sẻ mã này cho bất kỳ ai để bảo đảm an toàn.
                                </p>
                                
                                <hr style="border: none; border-top: 1px solid #EAEAEA; margin: 25px 0;" />
                                
                                <p style="font-size: 13px; color: #888888; text-align: center; margin-bottom: 0;">
                                    Trân trọng,<br/><strong>Hệ thống E-Commerce</strong>
                                </p>
                            </div>
                        </div>
                    </div>
                    """;

            // Thay thế chữ {{OTP_CODE}} bằng mã OTP thật
            String htmlContent = htmlTemplate.replace("{{OTP_CODE}}", otpCode);

            helper.setText(htmlContent, true);

            javaMailSender.send(message);
            log.info("📧 Đã gửi email HTML chứa OTP thành công tới: {}", toEmail);

        } catch (Exception e) {
            log.error("❌ Lỗi khi gửi email tới {}: {}", toEmail, e.getMessage(), e);
            throw new RuntimeException("Gửi email thất bại tới " + toEmail + ": " + e.getMessage(), e);
        }
    }
}