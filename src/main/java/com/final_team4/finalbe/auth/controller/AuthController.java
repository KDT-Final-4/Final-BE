package com.final_team4.finalbe.auth.controller;


import com.final_team4.finalbe.auth.dto.request.LoginRequest;
import com.final_team4.finalbe.auth.dto.response.LoginResponse;
import com.final_team4.finalbe.auth.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

  private final AuthService authService;

    @Value("${cookie.secure:false}") // 기본값 false
    private boolean cookieSecure;

  @ResponseStatus(HttpStatus.OK)
  @Operation(summary = "이메일/비밀번호 로그인")
  @PostMapping("/login")
  public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {

      LoginResponse response = authService.login(request);
      Duration ttl = Duration.between(response.issuedAt(), response.expiresAt());



      ResponseCookie cookie = ResponseCookie.from("ACCESS_TOKEN", response.accessToken())
              .httpOnly(true)  //클라이언트 JS에서 이 쿠키를 못건드리게 막음
              .secure(cookieSecure) //HTTPS 환경이면 true, 로컬에서 필요하면 false
              .path("/") //해당 도메인의 모든 URL에서 다 전송됨
              .sameSite("Lax") //다른 사이트에서 fetch 날리는 POST에는 쿠키 안붙음
              .maxAge(ttl)
              .build();

      ResponseCookie issuedAtCookie = ResponseCookie.from("ACCESS_ISSUED_AT", response.issuedAt().toString())
              .httpOnly(true)
              .secure(cookieSecure)
              .path("/")
              .sameSite("Lax")
              .maxAge(ttl)
              .build();

      ResponseCookie expiresAtCookie = ResponseCookie.from("ACCESS_EXPIRES_AT", response.expiresAt().toString())
              .httpOnly(true)
              .secure(cookieSecure)
              .path("/")
              .sameSite("Lax")
              .maxAge(ttl)
              .build();


      return ResponseEntity
              .ok()
              .header(HttpHeaders.SET_COOKIE, cookie.toString())
              .header(HttpHeaders.SET_COOKIE, issuedAtCookie.toString())
              .header(HttpHeaders.SET_COOKIE, expiresAtCookie.toString())
              .body(response);
  }


}

