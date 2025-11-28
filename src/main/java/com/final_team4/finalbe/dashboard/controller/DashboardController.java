package com.final_team4.finalbe.dashboard.controller;

import com.final_team4.finalbe.dashboard.dto.DashboardStatusGetRequest;
import com.final_team4.finalbe.dashboard.dto.DashboardStatusGetResponse;
import com.final_team4.finalbe.dashboard.service.DashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {


    @Autowired
    private DashboardService dashboardService;

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/")
    public DashboardStatusGetResponse getStatus(DashboardStatusGetRequest request){

        return dashboardService.getStatus(request);

    }

}
