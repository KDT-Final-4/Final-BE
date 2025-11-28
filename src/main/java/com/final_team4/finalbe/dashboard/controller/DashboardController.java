package com.final_team4.finalbe.dashboard.controller;

import com.final_team4.finalbe.dashboard.dto.DashboardStatusGetResponseDto;
import com.final_team4.finalbe.dashboard.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/status")
    public DashboardStatusGetResponseDto getStatus() {

        return dashboardService.getStatus();

    }

}
