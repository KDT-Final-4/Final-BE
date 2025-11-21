package com.final_team4.finalbe.user.controller;

import com.final_team4.finalbe._core.response.ApiResponse;
import com.final_team4.finalbe.user.dto.UserRegisterRequestDto;
import com.final_team4.finalbe.user.dto.response.UserSummaryResponse;
import com.final_team4.finalbe.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

}
