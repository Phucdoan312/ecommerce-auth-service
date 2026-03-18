package com.dvtp.authservice.application.port;

import com.dvtp.authservice.domain.entity.User;
import java.util.UUID;

public interface JwtTokenProvider {
    String generateToken(User user);
    String generateRefreshToken(User user);
    long getExpirationTime();
    long getRefreshTokenExpirationTime();
    UUID getUserIdFromToken(String token);
    boolean validateToken(String token);
}