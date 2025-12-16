package com.final_team4.finalbe._core.config;

import com.final_team4.finalbe._core.jwt.JwtAuthenticationFilter;
import com.final_team4.finalbe._core.jwt.JwtProperties;
import com.final_team4.finalbe._core.jwt.JwtTokenService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Value;
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

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
@EnableConfigurationProperties(JwtProperties.class)
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtTokenService jwtTokenService;

    @Value("${front-url:}")
    private String frontUrl;

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() {
        return new JwtAuthenticationFilter(jwtTokenService);
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers(
                                "/api/user/register",
                                "/api/auth/login",
                                "/swagger-ui/**",
                                "/v3/api-docs/**",
                                "/api/link/**",
                                "/actuator/health")  // ALB 헬스 체크용
                        .permitAll()
                        .anyRequest()
                        .authenticated())
                .exceptionHandling(exceptions -> exceptions
                        .authenticationEntryPoint((request, response, authException) ->
                                response.sendError(HttpServletResponse.SC_UNAUTHORIZED)));

        http.addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        
        // 프론트엔드 도메인 목록 (명시적 도메인)
        List<String> allowedOrigins = new java.util.ArrayList<>(Arrays.asList(
                "https://aura-ai.site",
                "https://www.aura-ai.site",
                "https://final-fe-fork.vercel.app"  // Vercel 기본 도메인
        ));
        
        // 환경 변수로 설정된 프론트 URL이 있으면 추가
        if (frontUrl != null && !frontUrl.isEmpty() && !allowedOrigins.contains(frontUrl)) {
            allowedOrigins.add(frontUrl);
        }
        
        // 명시적인 도메인 설정 (allowCredentials와 함께 사용 가능)
        configuration.setAllowedOrigins(allowedOrigins);
        
        // Vercel 프리뷰 URL 패턴 허용 (예: https://final-fe-fork-abc123.vercel.app)
        // 패턴을 사용하면 allowCredentials와 함께 사용 가능 (Spring 5.3+)
        configuration.setAllowedOriginPatterns(Arrays.asList(
                "https://final-fe-fork-*.vercel.app",  // 프로젝트별 프리뷰 URL
                "https://*.vercel.app"  // 모든 Vercel 프리뷰 도메인 허용
        ));
        
        // 인증 정보(쿠키, Authorization 헤더) 허용
        // setAllowedOriginPatterns를 사용하면 allowCredentials(true)도 함께 사용 가능
        configuration.setAllowCredentials(true);
        
        // 허용할 HTTP 메서드
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
        
        // 허용할 헤더
        configuration.setAllowedHeaders(Arrays.asList("*"));
        
        // Preflight 요청 캐시 시간
        configuration.setMaxAge(3600L);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
