package com.final_team4.finalbe.dashboard.service;


import com.final_team4.finalbe.dashboard.controller.DashboardController;
import com.final_team4.finalbe.dashboard.dto.DashboardStatusGetRequest;
import com.final_team4.finalbe.dashboard.dto.DashboardStatusGetResponse;
import com.final_team4.finalbe.dashboard.mapper.ClicksMapper;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.service.GenericResponseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DashboardService {

    private ClicksMapper clicksMapper;

    public DashboardStatusGetResponse getStatus(DashboardStatusGetRequest request) {
        DashboardStatusGetResponse response = new DashboardStatusGetResponse();
        long clicks = clicksMapper.countAllClicks();
        return response;
    };
}
