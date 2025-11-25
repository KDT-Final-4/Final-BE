package com.final_team4.finalbe.user.controller;

import com.final_team4.finalbe._core.response.ApiResponse;
import com.final_team4.finalbe._core.security.JwtPrincipal;
import com.final_team4.finalbe.user.dto.UserRegisterRequestDto;
import com.final_team4.finalbe.user.dto.response.UserSummaryResponse;
import com.final_team4.finalbe.user.service.UserService;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.hc.core5.http.HttpHeaders;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    @Value("${cookie.secure:false}") // 미설정 시 false
    private boolean cookieSecure;

    private static final List<String> ACCESS_COOKIES = List.of(
            "ACCESS_TOKEN", "ACCESS_EXPIRES_AT", "ACCESS_ISSUED_AT");

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/register")
    public UserSummaryResponse register(
            @Valid @RequestBody UserRegisterRequestDto request) {
        return userService.register(request);
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/me")
    public UserSummaryResponse me(
            @AuthenticationPrincipal JwtPrincipal principal) {
        return userService.findSummary(principal.userId());

    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PostMapping("/logout")
    public void logout(@AuthenticationPrincipal JwtPrincipal principal,HttpServletResponse response) {

        SecurityContextHolder.clearContext(); // 선택적: 현재 요청 컨텍스트 정리
        ACCESS_COOKIES.forEach(name->addExpiredCookie(response,name));

    }

    private void addExpiredCookie(HttpServletResponse response, String name) {
        ResponseCookie cookie = ResponseCookie.from(name,"")
                .httpOnly(true)
                .secure(cookieSecure)
                .path("/")
                .sameSite("Lax")
                .maxAge(0)
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }
}
