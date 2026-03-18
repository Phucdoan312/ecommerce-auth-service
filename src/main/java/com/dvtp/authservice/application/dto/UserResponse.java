package com.dvtp.authservice.application.dto;

import com.dvtp.authservice.domain.entity.User;
import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public record UserResponse(
        UUID id,
        String username,
        String email,
        LocalDate dob,
        String phone,
        boolean enabled,
        boolean dobUpdated,
        Set<String> roles
) {
    // Factory method để map từ Domain Entity sang Record dễ dàng
    public static UserResponse fromDomain(User user) {
        return new UserResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getDob(),
                user.getPhone(),
                user.isEnabled(),
                user.isDobUpdated(),
                user.getRoles().stream()
                        .map(role -> role.getName())
                        .collect(Collectors.toSet())
        );
    }
}