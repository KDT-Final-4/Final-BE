package com.final_team4.finalbe.auth.controller;

import com.final_team4.finalbe.auth.dto.request.LoginRequest;
import com.final_team4.finalbe.auth.dto.response.LoginResponse;
import com.final_team4.finalbe.auth.dto.response.TokenResponse;
import com.final_team4.finalbe.auth.service.AuthService;
import com.final_team4.finalbe._core.jwt.JwtToken;
import com.final_team4.finalbe._core.jwt.JwtTokenService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

  private final AuthService authService;
  private final JwtTokenService jwtTokenService;

  @ResponseStatus(HttpStatus.OK)
  @Operation(summary = "이메일/비밀번호 로그인")
  @PostMapping("/login")
  public LoginResponse login(@Valid @RequestBody LoginRequest request) {
    return authService.login(request);
  }

}
