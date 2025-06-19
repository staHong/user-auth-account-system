package com.ds.auth.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.ds.auth.util.JwtFilter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

@Configuration
@EnableWebSecurity  // Spring Security 설정 클래스
public class SecurityConfig {

    private final JwtFilter jwtFilter;

    public SecurityConfig(JwtFilter jwtFilter) {
        this.jwtFilter = jwtFilter;
    }

    // application.properties에서 인증이 필요한 URL 목록 가져오기
    @Value("${security.auth-required}")
    private String authRequiredPath;

    /**
     * Spring Security 설정을 구성하는 SecurityFilterChain Bean을 생성합니다.
     *
     * @param http HttpSecurity 객체로, 보안 설정을 정의하는 데 사용됩니다.
     * @return SecurityFilterChain 객체를 반환하여 Spring Security가 적용될 필터 체인을 설정합니다.
     * @throws Exception 설정 중 발생할 수 있는 예외를 처리합니다.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable) // CSRF 보안 비활성화
                .cors(cors -> cors.configurationSource(corsConfigurationSource())) // CORS 설정 추가
                .sessionManagement(session -> session.sessionCreationPolicy(STATELESS)) // 세션 사용 안함 (JWT 사용 시 필수)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(authRequiredPath).authenticated() // 인증이 필요한 경로
                        .anyRequest().permitAll() // 나머지 모든 요청은 인증 없이 허용
                )
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class); // JWT 필터 추가

        return http.build();
    }

    /**
     * CORS 설정 추가 (Spring Boot 3.x / Security 6.x 최적화)
     * - 모든 도메인에서 API 요청을 보낼 수 있도록 설정
     * - 프론트엔드가 다른 도메인이어도 허용
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.addAllowedOrigin("http://localhost:5173");
        config.addAllowedOrigin("http://localhost:5174");
        config.addAllowedMethod("*"); // 모든 HTTP 메서드 허용
        config.addAllowedHeader("*"); // 모든 헤더 허용
        config.setAllowCredentials(true); // JWT 토큰을 사용할 경우 필수

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    /**
     * 비밀번호 암호화를 위한 BCryptPasswordEncoder 빈 등록
     * @return PasswordEncoder (BCrypt 방식 사용)
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
