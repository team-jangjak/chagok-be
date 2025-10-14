package com.jangjak.chagok.common.security;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    @Value("${app.frontend-base-url}")
    private String frontendBaseUrl;

    private final JwtFilter jwtFilter; // JWT 필터
    private final CustomAuthenticationEntryPoint entryPoint; // 인증 실패 응답용
    private final CustomAccessDeniedHandler accessDeniedHandler; // 인가 실패 응답용

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> {
                })
                // Cross-Site Request Forgery - 사용자가 의도하지 않은 요청을 보내도록 속이는 공격 -> JWT 인증 방식이므로 비활성화
                .csrf(AbstractHttpConfigurer::disable)
                // 세션을 사용하지 않으므로 비활성화
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // Authorization 헤더에 Basic 아이디:비번을 담는 구조 -> JWT 인증 방식(Bearer)이므로 비활성화
                .httpBasic(AbstractHttpConfigurer::disable)
                // FE 쪽 form 방식 로그인 기반 인증 -> REST API + JSON 기반 인증 방식이므로 비활성화
                .formLogin(AbstractHttpConfigurer::disable)
                // 허용 URI 설정
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/user/kakao-login", "/user/google-login", "/user/google-login-view",
                                "/user/sign-up", "/user/reissue", "/habit/popular-habit-category"
                        ).permitAll()
                        .anyRequest().authenticated()
                )
                // 인증/인가 실패에 대한 처리
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(entryPoint) // 인증
                        .accessDeniedHandler(accessDeniedHandler) // 인가
                )
                // JWT 인증 우선 처리
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of(frontendBaseUrl));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true); // 쿠키 허용

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }


}
