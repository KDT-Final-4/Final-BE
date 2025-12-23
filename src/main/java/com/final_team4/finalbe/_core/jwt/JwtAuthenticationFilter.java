package com.final_team4.finalbe._core.jwt;

import com.final_team4.finalbe._core.security.AccessCookieManager;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

public class JwtAuthenticationFilter extends OncePerRequestFilter {



  private final JwtTokenService jwtTokenService;

  private final AccessCookieManager accessCookieManager;

  public JwtAuthenticationFilter(JwtTokenService jwtTokenService, AccessCookieManager accessCookieManager) {
    this.jwtTokenService = jwtTokenService;
      this.accessCookieManager = accessCookieManager;
  }

  @Override
  protected void doFilterInternal(
          //HTTP 요청이 들어올 때마다 JWT 토큰을 꺼꺼내서 유효성 검증,
          // 인증 정보(Authentication)를 SecurityContextHolder에 넣어주는 필터 역할임
      HttpServletRequest request,
      HttpServletResponse response,
      FilterChain filterChain) throws ServletException, IOException {
      try {
          String token = extractToken(request);
          if (token != null && SecurityContextHolder.getContext().getAuthentication() == null) {
              Authentication authentication = jwtTokenService.authenticate(token);
              SecurityContext context = SecurityContextHolder.createEmptyContext();
              context.setAuthentication(authentication);
              SecurityContextHolder.setContext(context);
          }
          filterChain.doFilter(request, response);
      }
          catch (JwtException | IllegalArgumentException exception) {
              SecurityContextHolder.clearContext();
              accessCookieManager.clearAccessCookies(response);
              writeUnauthorized(response);
          }

      }

  private String extractToken(HttpServletRequest request) {

      Cookie[] cookies = request.getCookies();
      if (cookies != null) {
          for (Cookie cookie : cookies) {
              if ("ACCESS_TOKEN".equals(cookie.getName())) {
                  String token = cookie.getValue();
                  if (StringUtils.hasText(token)) {
                      return token.trim();
                  }
              }
          }
      }

      // 3) 둘 다 없으면 null
      return null;
  }

  private void writeUnauthorized(HttpServletResponse response) throws IOException {
      response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
      response.setContentType("application/json;charset=UTF-8");
      response.getWriter().write("""
            {"code":"AUTH_TOKEN_EXPIRED","message":"로그인이 만료되었습니다. 다시 로그인해 주세요."}
        """);
  }
}
