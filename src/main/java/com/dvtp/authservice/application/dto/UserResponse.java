package com.dvtp.authservice.application.dto;

import com.dvtp.authservice.domain.entity.User;
import java.time.LocalDate;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public record UserResponse(
        UUID id,
        String username,
        String email,
        LocalDate dob,
        String phone,
        boolean enabled,
        boolean dobUpdated,
        Map<String, Set<String>> appRoles
) {

    public static UserResponse fromDomain(User user) {
        return new UserResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getDob(),
                user.getPhone(),
                user.isEnabled(),
                user.isDobUpdated(),
                    user.getAppRoles()
        );
    }
}