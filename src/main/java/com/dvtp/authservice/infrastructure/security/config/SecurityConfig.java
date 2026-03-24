package com.dvtp.authservice.infrastructure.security.config;

import com.dvtp.authservice.infrastructure.security.jwt.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // 1. KÍCH HOẠT CORS: Trỏ xuống cái Bean cấu hình ở dưới cùng
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))

                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()
                        .anyRequest().authenticated()
                )

                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    // 2. CẤU HÌNH CORS CHI TIẾT
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // Danh sách các Frontend được phép gọi vào Backend (Không có dấu / ở cuối link nhé)
        configuration.setAllowedOrigins(List.of(
                "http://localhost:5173", // Frontend Vite 1 (E-commerce)
                "http://localhost:5174", // Frontend Vite 2 (Bank Simulator)
                "http://localhost:3000"  // Frontend React cũ (nếu có)
                // 🚀 BÍ KÍP: Sau này deploy Frontend lên Vercel/Netlify thì thêm cái link web đó vào đây!
        ));

        // Mở cửa cho mọi hành động (GET, POST, PUT, DELETE, OPTIONS...)
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));

        // Chấp nhận mọi loại Header (bao gồm cả Authorization chứa Token và các Header rườm rà của Axios)
        configuration.setAllowedHeaders(List.of("*"));

        // Cho phép Frontend lấy được Token từ Backend trả về
        configuration.setExposedHeaders(List.of("Authorization"));

        // Cho phép gửi kèm Credentials (cực kỳ quan trọng nếu Frontend dùng Axios set withCredentials = true)
        configuration.setAllowCredentials(true);

        // Áp dụng luật này cho tất cả API (từ thư mục gốc /** trở đi)
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }
}