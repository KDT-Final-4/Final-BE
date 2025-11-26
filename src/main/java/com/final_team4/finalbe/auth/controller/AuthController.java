package com.final_team4.finalbe.auth.controller;


import com.final_team4.finalbe._core.security.AccessCookieManager;
import com.final_team4.finalbe._core.security.AccessTokenPayload;
import com.final_team4.finalbe.auth.dto.request.LoginRequest;
import com.final_team4.finalbe.auth.dto.response.LoginResponse;
import com.final_team4.finalbe.auth.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

  private final AuthService authService;
    private final AccessCookieManager accessCookieManager;



  @ResponseStatus(HttpStatus.OK)
  @Operation(summary = "이메일/비밀번호 로그인")
  @PostMapping("/login")
  public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request,HttpServletResponse response) {

      LoginResponse loginResponse = authService.login(request);
      accessCookieManager.clearAccessCookies(response); //이전 쿠키 정리
      accessCookieManager.setAccessCookies(response, AccessTokenPayload.from(
              loginResponse.accessToken()));//헤더에 새 쿠키 설정
      return ResponseEntity.ok(loginResponse);
  }

}

