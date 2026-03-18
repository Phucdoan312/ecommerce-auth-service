package com.dvtp.authservice.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import java.time.LocalDate;

public record UpdateProfileCommand(
        @Schema(example = "0912345678", description = "Số điện thoại Việt Nam")
        @Pattern(regexp = "^(0|84)(3|5|7|8|9)[0-9]{8}$", message = "Số điện thoại không đúng định dạng")
        String phone,

        @Schema(example = "2000-01-01", description = "Ngày sinh (chỉ được đổi 1 lần)")
        @Past(message = "Ngày sinh phải là một ngày trong quá khứ")
        LocalDate dob
) {}