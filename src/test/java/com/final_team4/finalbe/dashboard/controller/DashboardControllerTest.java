package com.final_team4.finalbe.dashboard.controller;


import com.final_team4.finalbe._core.jwt.JwtTokenService;
import com.final_team4.finalbe._core.security.AccessCookieManager;
import com.final_team4.finalbe.content.mapper.ContentMapper;
import com.final_team4.finalbe.dashboard.dto.DashboardStatusGetResponseDto;
import com.final_team4.finalbe.dashboard.mapper.ClicksMapper;
import com.final_team4.finalbe.dashboard.service.DashboardService;
import com.final_team4.finalbe.logger.aop.Loggable;
import com.final_team4.finalbe.logger.mapper.LoggerMapper;
import com.final_team4.finalbe.schedule.mapper.ScheduleMapper;
import com.final_team4.finalbe.schedule.mapper.ScheduleSettingMapper;
import com.final_team4.finalbe.setting.mapper.notification.NotificationCredentialMapper;
import com.final_team4.finalbe.trend.mapper.TrendMapper;
import com.final_team4.finalbe.uploadChannel.mapper.UploadChannelMapper;
import com.final_team4.finalbe.user.mapper.UserInfoMapper;
import com.final_team4.finalbe.user.mapper.UserMapper;
import com.final_team4.finalbe.user.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = DashboardController.class)
@AutoConfigureMockMvc(addFilters = false) // SecurityFilterChain 무시, 필터까지 검증할 거면 제거하고 @WithMockUser 사용
public class DashboardControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private DashboardService dashboardService;

    @MockitoBean
    ClicksMapper clicksMapper;

    @MockitoBean
    UserService userService;

    @MockitoBean
    UserMapper userMapper;

    @MockitoBean
    UserInfoMapper userInfoMapper;

    @MockitoBean
    ScheduleMapper scheduleMapper;

    @MockitoBean
    ScheduleSettingMapper scheduleSettingMapper;

    @MockitoBean
    TrendMapper trendMapper;

    @MockitoBean
    UploadChannelMapper uploadChannelMapper;

    @MockitoBean
    Loggable loggable;

    @MockitoBean
    LoggerMapper loggerMapper;

    @MockitoBean
    AccessCookieManager accessCookieManager;

    @MockitoBean
    JwtTokenService jwtTokenService;

    @MockitoBean
    NotificationCredentialMapper notificationCredentialMapper;

    @MockitoBean
    ContentMapper contentMapper;

    @Test
    @DisplayName("대시보드 상태 조회는 200과 json 숫자 allClicks및 타 숫자들을 반환")
    void getStatus_returnsJsonWithAllClicks() throws Exception {
        DashboardStatusGetResponseDto response = DashboardStatusGetResponseDto.builder()
                .allClicks(123L)
                .allViews(0L)
                .visitors(0L)
                .averageDwellTime(0L)
                .build();

        given(dashboardService.getStatus()).willReturn(response);

        mockMvc.perform(get("/api/dashboard/status"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.allClicks").isNumber())
                .andExpect(jsonPath("$.allClicks").value(123));
    }
}

