package com.final_team4.finalbe.dashboard.controller;

import com.final_team4.finalbe._core.security.JwtPrincipal;
import com.final_team4.finalbe.dashboard.dto.DashboardContentsResponseDto;
import com.final_team4.finalbe.dashboard.dto.DashboardStatusGetResponseDto;
import com.final_team4.finalbe.dashboard.service.DashboardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @Operation(
            summary = "대시보드 상태 조회",
            description = "전체 클릭수(이것만 구현됨), 조회수, 방문자수, 평균 체류 시간을 조회합니다."
    )
    @ApiResponse(responseCode = "200", description = "조회 성공")
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/status")
    public DashboardStatusGetResponseDto getStatus() {

        return dashboardService.getStatus();

    }

    @ApiResponse(responseCode = "200", description = "조회 성공")
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/contents")
    public DashboardContentsResponseDto getContents(@AuthenticationPrincipal JwtPrincipal principal) {

        return dashboardService.getContents(principal.userId());
    }
}
