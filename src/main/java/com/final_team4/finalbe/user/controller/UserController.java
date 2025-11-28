package com.final_team4.finalbe.user.controller;

import com.final_team4.finalbe._core.exception.UnauthorizedException;
import com.final_team4.finalbe._core.jwt.JwtToken;
import com.final_team4.finalbe._core.jwt.JwtTokenService;
import com.final_team4.finalbe._core.security.AccessCookieManager;
import com.final_team4.finalbe._core.security.AccessTokenPayload;
import com.final_team4.finalbe._core.security.JwtPrincipal;
import com.final_team4.finalbe.user.domain.User;
import com.final_team4.finalbe.user.dto.PasswordUpdateRequest;
import com.final_team4.finalbe.user.dto.UserRegisterRequestDto;
import com.final_team4.finalbe.user.dto.UserUpdateRequest;
import com.final_team4.finalbe.user.dto.response.UserFullResponse;
import com.final_team4.finalbe.user.dto.response.UserSummaryResponse;
import com.final_team4.finalbe.user.dto.response.UserUpdateResponse;
import com.final_team4.finalbe.user.mapper.UserInfoMapper;
import com.final_team4.finalbe.user.service.UserService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;



@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final JwtTokenService jwtTokenService;
    private final AccessCookieManager accessCookieManager;


    private Long requireUserId(JwtPrincipal principal) {
        if (principal == null) {
            throw new UnauthorizedException("인증 정보가 없습니다.");
        }
        return principal.userId();
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/register")
    public UserSummaryResponse register(
            @Valid @RequestBody UserRegisterRequestDto request) {

        return userService.register(request);

    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/me")
    public UserFullResponse me(
            @AuthenticationPrincipal JwtPrincipal principal) {

        Long userId = requireUserId(principal);
        return userService.findProfile(userId);

    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PostMapping("/logout")
    public void logout(@AuthenticationPrincipal JwtPrincipal principal,HttpServletResponse response) {
        //principal을 주입한 이유는 추후에 로그아웃시 토큰만료까지 가능하게 할 때 필요할 듯 해서

        SecurityContextHolder.clearContext(); // 선택적: 현재 요청 컨텍스트 정리
        accessCookieManager.clearAccessCookies(response);

    }

    @ResponseStatus(HttpStatus.OK)
    @PatchMapping("/update")
    public UserUpdateResponse update(
            @AuthenticationPrincipal JwtPrincipal principal,
            @Valid @RequestBody UserUpdateRequest request,
            HttpServletResponse response) {

        Long userId = requireUserId(principal);
        User updatedUser = userService.updateProfile(userId,request); // userId로 변경요청사항 업데이트

        JwtToken token = jwtTokenService.issueToken(updatedUser);  // 업데이트된 유저로 새 토큰 발급

        accessCookieManager.clearAccessCookies(response);  // 기존 쿠키 삭제
        accessCookieManager.setAccessCookies(response, AccessTokenPayload.from(token)); // 새 토큰을 쿠키에 넣어줌

        Authentication auth = jwtTokenService.authenticate(token.value()); // 토큰에 권한 부여(이 때 principal 업데이트)
        SecurityContextHolder.getContext().setAuthentication(auth); // 시큐리티 컨텍스트에 인증 정보를 설정

        return UserInfoMapper.toUserUpdateResponse(updatedUser);
        //
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PatchMapping("/password")
    public void updatePassword(@Valid @RequestBody PasswordUpdateRequest request,
                               HttpServletResponse response, @AuthenticationPrincipal JwtPrincipal principal) {

        Long userId = requireUserId(principal);
        userService.updatePassword(request,userId);

        SecurityContextHolder.clearContext(); // 선택적: 현재 요청 컨텍스트 정리
        accessCookieManager.clearAccessCookies(response);  // 기존 쿠키 삭제

    };
}
