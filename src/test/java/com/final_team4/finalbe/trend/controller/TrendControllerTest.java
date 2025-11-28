package com.final_team4.finalbe.trend.controller;

import com.final_team4.finalbe._core.security.JwtPrincipal;
import com.final_team4.finalbe.content.mapper.ContentMapper;
import com.final_team4.finalbe.logger.aop.Loggable;
import com.final_team4.finalbe.logger.mapper.LoggerMapper;
import com.final_team4.finalbe.schedule.mapper.ScheduleMapper;
import com.final_team4.finalbe.schedule.mapper.ScheduleSettingMapper;
import com.final_team4.finalbe.setting.mapper.notification.NotificationCredentialMapper;
import com.final_team4.finalbe.trend.dto.TrendCreateContentRequestDto;
import com.final_team4.finalbe.trend.dto.TrendCreateContentResponseDto;
import com.final_team4.finalbe.trend.dto.TrendCreateRequestDto;
import com.final_team4.finalbe.trend.dto.TrendCreateResponseDto;
import com.final_team4.finalbe.trend.dto.TrendResponseDto;
import com.final_team4.finalbe.trend.mapper.TrendMapper;
import com.final_team4.finalbe.trend.service.TrendService;
import com.final_team4.finalbe.uploadChannel.mapper.UploadChannelMapper;
import com.final_team4.finalbe.user.mapper.UserMapper;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mybatis.spring.boot.autoconfigure.MybatisAutoConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.security.web.method.annotation.AuthenticationPrincipalArgumentResolver;
import org.junit.jupiter.api.AfterEach;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// TrendController 슬라이스를 띄우되 Security/MyBatis 자동구성은 제외한다.
@WebMvcTest(value = TrendController.class,
        excludeAutoConfiguration = {
                SecurityAutoConfiguration.class,
                MybatisAutoConfiguration.class
        }
)
class TrendControllerTest {

    @Autowired
    MockMvc mockMvc;

    // 컨트롤러가 의존하는 빈들을 Mockito로 대체하여 DB 없이도 동작하도록 한다.
    @MockitoBean TrendService trendService;
    @MockitoBean Loggable loggable;
    @MockitoBean ContentMapper contentMapper;
    @MockitoBean LoggerMapper loggerMapper;
    @MockitoBean ScheduleMapper scheduleMapper;
    @MockitoBean ScheduleSettingMapper scheduleSettingMapper;
    @MockitoBean NotificationCredentialMapper notificationCredentialMapper;
    @MockitoBean TrendMapper trendMapper;
    @MockitoBean UploadChannelMapper uploadChannelMapper;
    @MockitoBean UserMapper userMapper;

    // 테스트간 Authentication이 공유되지 않도록 매번 SecurityContext 초기화
    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    // @AuthenticationPrincipal이 작동하도록 ArgumentResolver를 수동 등록한다.
    @TestConfiguration
    static class AuthenticationPrincipalResolverConfig implements WebMvcConfigurer {
        @Override
        public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
            resolvers.add(new AuthenticationPrincipalArgumentResolver());
        }
    }

    @Test
    @DisplayName("인기검색어 생성 성공 시 201 Created와 응답 본문 반환")
    void createTrend_success() throws Exception {
        // given
        TrendCreateResponseDto responseDto = TrendCreateResponseDto.builder()
                .id(4L)
                .categoryId(2L)
                .keyword("beauty")
                .searchVolume(900L)
                .snsType("INSTAGRAM")
                .build();
        given(trendService.createTrend(any(TrendCreateRequestDto.class)))
                .willReturn(responseDto);

        String requestBody = """
                {
                  "categoryId": 2,
                  "keyword": "beauty",
                  "searchVolume": 900,
                  "snsType": "INSTAGRAM"
                }
                """;

        // when & then
        mockMvc.perform(post("/api/trend")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(4))
                .andExpect(jsonPath("$.categoryId").value(2))
                .andExpect(jsonPath("$.keyword").value("beauty"));

        verify(trendService).createTrend(any(TrendCreateRequestDto.class));
    }

    @Test
    @DisplayName("인기검색어 목록 조회 성공 시 200 OK와 목록 반환")
    void getTrends_success() throws Exception {
        // given
        List<TrendResponseDto> responses = List.of(
                TrendResponseDto.builder().id(1L).categoryId(1L).keyword("keyword-1").searchVolume(300L).snsType("X").build(),
                TrendResponseDto.builder().id(2L).categoryId(1L).keyword("keyword-2").searchVolume(200L).snsType("Y").build()
        );
        given(trendService.getTrends(0, 2)).willReturn(responses);

        // when & then
        mockMvc.perform(get("/api/trend")
                        .param("page", "0")
                        .param("size", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].keyword").value("keyword-1"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].snsType").value("Y"));

        verify(trendService).getTrends(0, 2);
    }

    @Test
    @DisplayName("컨텐츠 생성 요청 성공 시 202 Accepted와 응답 반환")
    void requestCreateContent_success() throws Exception {
        // given
        TrendCreateContentResponseDto responseDto = TrendCreateContentResponseDto.of("fashion", true);
        given(trendService.requestCreateContent(any(TrendCreateContentRequestDto.class), eq(99L)))
                .willReturn(responseDto);

        UserDetails principal = principalOf(99L);
        String requestBody = """
                {
                  "keyword": "fashion"
                }
                """;

        // when & then
        mockMvc.perform(post("/api/trend/content")
                        .with(authentication(authToken(principal)))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isAccepted())
                .andExpect(jsonPath("$.keyword").value("fashion"))
                .andExpect(jsonPath("$.requested").value(true));

        verify(trendService).requestCreateContent(any(TrendCreateContentRequestDto.class), eq(99L));
    }

    // JWT 주체 정보를 간단히 만들어주는 헬퍼 메서드
    private UserDetails principalOf(Long userId) {
        return JwtPrincipal.builder()
                .userId(userId)
                .email("user" + userId + "@example.com")
                .name("user" + userId)
                .role("ROLE_USER")
                .authorities(List.of(new SimpleGrantedAuthority("ROLE_USER")))
                .accountNonExpired(true)
                .accountNonLocked(true)
                .credentialsNonExpired(true)
                .enabled(true)
                .build();
    }

    // SecurityContext에 넣어줄 Authentication 토큰을 생성한다.
    private UsernamePasswordAuthenticationToken authToken(UserDetails principal) {
        return new UsernamePasswordAuthenticationToken(principal, "token", principal.getAuthorities());
    }
}
