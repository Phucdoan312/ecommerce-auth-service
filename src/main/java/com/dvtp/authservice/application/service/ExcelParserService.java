package com.dvtp.authservice.application.service;

import com.dvtp.authservice.domain.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ExcelParserService {

    private final PasswordEncoder passwordEncoder;

    public List<User> parseExcelFile(MultipartFile file) {
        List<User> users = new ArrayList<>();
        // 🚀 VŨ KHÍ TỐI THƯỢNG ĐÂY RỒI:
        DataFormatter formatter = new DataFormatter();

        try (InputStream inputStream = file.getInputStream();
             Workbook workbook = new XSSFWorkbook(inputStream)) {

            Sheet sheet = workbook.getSheetAt(0);

            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;

                // 🚀 Dùng formatter thay vì getStringCellValue()
                // Nó sẽ tự động biến mọi thứ thành String và không bao giờ báo lỗi
                String username = formatter.formatCellValue(row.getCell(0));
                String email = formatter.formatCellValue(row.getCell(1));
                String phone = formatter.formatCellValue(row.getCell(2));
                String rawPassword = formatter.formatCellValue(row.getCell(3));

                // Chặn trường hợp Excel tự động tạo mấy dòng trắng ở cuối file
                if (username.trim().isEmpty() || email.trim().isEmpty()) {
                    continue;
                }

                User user = User.builder()
                        .id(UUID.randomUUID())
                        .username(username)
                        .email(email)
                        .phone(phone)
                        .password(passwordEncoder.encode(rawPassword))
                        .enabled(true)
                        .build();

                users.add(user);
            }
            log.info("Đã trích xuất thành công {} users từ file Excel", users.size());

        } catch (Exception e) {
            // In ra cái lỗi thật sự để dev dễ debug
            log.error("🔥 Lỗi thực sự khi đọc Excel: ", e);
            throw new RuntimeException("Không thể đọc file Excel. Lỗi chi tiết: " + e.getMessage());
        }

        return users;
    }
}