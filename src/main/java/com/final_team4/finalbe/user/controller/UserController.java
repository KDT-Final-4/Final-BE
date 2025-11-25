package com.final_team4.finalbe.user.controller;

import com.final_team4.finalbe._core.response.ApiResponse;
import com.final_team4.finalbe._core.security.JwtPrincipal;
import com.final_team4.finalbe.user.dto.UserRegisterRequestDto;
import com.final_team4.finalbe.user.dto.response.UserSummaryResponse;
import com.final_team4.finalbe.user.service.UserService;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<UserSummaryResponse>> register(
            @Valid @RequestBody UserRegisterRequestDto request) {
        UserSummaryResponse createdUser = userService.register(request);
        return ResponseEntity.ok(ApiResponse.ok("회원가입이 완료됐습니다.", createdUser));
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserSummaryResponse>> me(
            @AuthenticationPrincipal JwtPrincipal principal) {
        UserSummaryResponse me = userService.findSummary(principal.userId());
        return ResponseEntity.ok(ApiResponse.ok("내 정보", me));
    }

}

