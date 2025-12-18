package com.final_team4.finalbe._core.config;

import com.final_team4.finalbe._core.jwt.JwtAuthenticationFilter;
import com.final_team4.finalbe._core.jwt.JwtProperties;
import com.final_team4.finalbe._core.jwt.JwtTokenService;
import com.final_team4.finalbe._core.security.AccessCookieManager;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.RequestMatcher;


@Configuration
@EnableWebSecurity
@EnableConfigurationProperties(JwtProperties.class)
@RequiredArgsConstructor
public class SecurityConfig {

    @Value("${internal.token:}")
    private String internalToken;

    private final JwtTokenService jwtTokenService;
    private final AccessCookieManager accessCookieManager;


    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() {
        return new JwtAuthenticationFilter(jwtTokenService,accessCookieManager);
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        RequestMatcher pythonMatcher = request -> {
            String token = request.getHeader("X-Internal-Token");
            return token != null && token.equals(internalToken);
        };
        http
                .csrf(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers(pythonMatcher).permitAll()//파이썬 서버 헤더가 있으면 무인증
                        .requestMatchers(
                                "/api/user/register",
                                "/api/auth/login",
                                "/swagger-ui/**",
                                "/v3/api-docs/**",
                                "/api/link/**")
                        .permitAll()
                        .anyRequest()
                        .authenticated())
                .exceptionHandling(exceptions -> exceptions
                        //인증이 안된 요청이 들어왔을 때 현재 Security Context를 비우고, 401, 응답메시지 반환
                        .authenticationEntryPoint((request, response, authException) ->{
                                    SecurityContextHolder.clearContext();
                                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                                    response.setContentType("application/json;charset=UTF-8");
                                    response.getWriter().write("""
                                                    {"code":"AUTH_TOKEN_EXPIRED","message":"로그인이 만료되었습니다. 다시 로그인해 주세요."}
                                            """);
                                }));

        http.addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
