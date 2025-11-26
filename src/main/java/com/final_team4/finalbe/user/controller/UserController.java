package com.final_team4.finalbe.user.controller;

import com.final_team4.finalbe._core.security.AccessCookieManager;
import com.final_team4.finalbe._core.security.JwtPrincipal;
import com.final_team4.finalbe.user.dto.UserRegisterRequestDto;
import com.final_team4.finalbe.user.dto.response.UserSummaryResponse;
import com.final_team4.finalbe.user.service.UserService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final AccessCookieManager accessCookieManager;

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
        //principal을 주입한 이유는 추후에 로그아웃시 토큰만료까지 가능하게 할 때 필요할 듯 해서

        SecurityContextHolder.clearContext(); // 선택적: 현재 요청 컨텍스트 정리
        accessCookieManager.clearAccessCookies(response);

    }

}
