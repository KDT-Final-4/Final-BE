package com.final_team4.finalbe.user.controller;

import com.final_team4.finalbe.user.dto.request.UserRegisterRequest;
import com.final_team4.finalbe.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
/*
    @PostMapping("/register")
    public ResponseEntity<ApiResponse> register(
            @Valid @RequestBody UserRegisterRequest request) {
        userService.register(request);
        ApiResponse body = ApiResponse.of("회원가입이 완료됐습니다.");
        return ResponseEntity.ok(body);
    }*/

    @PostMapping("/register")
    public void register(
            @Valid @RequestBody UserRegisterRequest request) {
        userService.register(request);
    }// 공통 응답 정해지면 수정할듯

}
