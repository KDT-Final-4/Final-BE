package com.final_team4.finalbe.dashboard.controller;


import com.final_team4.finalbe._core.jwt.JwtTokenService;
import com.final_team4.finalbe._core.security.AccessCookieManager;
import com.final_team4.finalbe._core.security.JwtPrincipal;
import com.final_team4.finalbe.content.mapper.ContentMapper;
import com.final_team4.finalbe.dashboard.dto.DashboardContentItemDto;
import com.final_team4.finalbe.dashboard.dto.DashboardContentsResponseDto;
import com.final_team4.finalbe.dashboard.dto.DashboardStatusGetResponseDto;
import com.final_team4.finalbe.dashboard.mapper.ClicksMapper;
import com.final_team4.finalbe.dashboard.mapper.DashboardMapper;
import com.final_team4.finalbe.dashboard.service.DashboardService;
import com.final_team4.finalbe.link.mapper.LinkMapper;
import com.final_team4.finalbe.logger.aop.Loggable;
import com.final_team4.finalbe.logger.mapper.LoggerMapper;
import com.final_team4.finalbe.notification.mapper.NotificationMapper;
import com.final_team4.finalbe.schedule.mapper.ScheduleMapper;
import com.final_team4.finalbe.schedule.mapper.ScheduleSettingMapper;
import com.final_team4.finalbe.setting.mapper.llm.LlmChannelMapper;
import com.final_team4.finalbe.setting.mapper.notification.NotificationCredentialMapper;
import com.final_team4.finalbe.trend.mapper.TrendMapper;
import com.final_team4.finalbe.uploadChannel.mapper.UploadChannelMapper;
import com.final_team4.finalbe.user.mapper.UserInfoMapper;
import com.final_team4.finalbe.user.mapper.UserMapper;
import com.final_team4.finalbe.user.service.UserService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
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

    @MockitoBean
    DashboardMapper dashboardMapper;

    @MockitoBean
    LlmChannelMapper llmChannelMapper;

    @MockitoBean
    NotificationMapper notificationMapper;

    @MockitoBean
    LinkMapper linkMapper;

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

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
    @Test
    @DisplayName("대시보드 콘텐츠 전체 조회는 인증 사용자 id로 서비스 호출하고 콘텐츠 목록을 반환")
    void getContents_returnsContentsForAuthenticatedUser() throws Exception {
        JwtPrincipal principal = testPrincipal(17L);
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        DashboardContentItemDto item = DashboardContentItemDto.builder()
                .contentId(10L)
                .title("테스트 콘텐츠")
                .keyword("키워드")
                .createdAt(LocalDateTime.of(2024, 1, 1, 12, 0))
                .updatedAt(LocalDateTime.of(2024, 1, 2, 12, 0))
                .link("http://example.com/content")
                .clickCount(5L)
                .build();

        DashboardContentsResponseDto response = DashboardContentsResponseDto.builder()
                .contents(List.of(item))
                .build();

        given(dashboardService.getContents(principal.userId())).willReturn(response);

        mockMvc.perform(get("/api/dashboard/contents").principal(authentication))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.contents[0].contentId").value(item.getContentId()))
                .andExpect(jsonPath("$.contents[0].title").value(item.getTitle()))
                .andExpect(jsonPath("$.contents[0].clickCount").value(item.getClickCount()));

        then(dashboardService).should().getContents(principal.userId());
    }

    private JwtPrincipal testPrincipal(Long userId) {
        return JwtPrincipal.builder()
                .userId(userId)
                .email("user" + userId + "@example.com")
                .name("tester")
                .role("MARKETER")
                .authorities(Collections.emptyList())
                .accountNonExpired(true)
                .accountNonLocked(true)
                .credentialsNonExpired(true)
                .enabled(true)
                .build();
    }
}


