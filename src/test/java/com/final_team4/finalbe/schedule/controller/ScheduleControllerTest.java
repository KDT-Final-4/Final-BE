package com.final_team4.finalbe.schedule.controller;

import com.final_team4.finalbe._core.exception.ContentNotFoundException;
import com.final_team4.finalbe._core.exception.PermissionDeniedException;
import com.final_team4.finalbe.schedule.domain.RepeatInterval;
import com.final_team4.finalbe.schedule.dto.schedule.ScheduleCreateResponseDto;
import com.final_team4.finalbe.schedule.dto.schedule.ScheduleDetailResponseDto;
import com.final_team4.finalbe.schedule.dto.schedule.ScheduleUpdateResponseDto;
import com.final_team4.finalbe.schedule.mapper.ScheduleMapper;
import com.final_team4.finalbe.schedule.service.ScheduleService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(
        controllers = ScheduleController.class,
        excludeAutoConfiguration = {SecurityAutoConfiguration.class} // Spring Security 자동 설정 제외
)
class ScheduleControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    ScheduleService scheduleService;

    @MockitoBean
    ScheduleMapper scheduleMapper;

    @DisplayName("일정 전체 조회 성공")
    @Test
    void findAll() throws Exception {
        //given
        List<ScheduleDetailResponseDto> mockResponse = new ArrayList<>();
        mockResponse.add(
                ScheduleDetailResponseDto.builder()
                        .id(1L)
                        .userId(1L)
                        .title("주간 팀 미팅")
                        .startTime(LocalDateTime.of(2025, 12, 1, 10, 0)) // 2025-12-01 10:00
                        .repeatInterval(RepeatInterval.WEEKLY)
                        .lastExecutedAt(null)
                        .createdAt(LocalDateTime.now())
                        .updatedAt(LocalDateTime.now())
                        .build()
        );
        mockResponse.add(
                ScheduleDetailResponseDto.builder()
                        .id(2L)
                        .userId(1L)
                        .title("매일관리")
                        .startTime(LocalDateTime.of(2025, 12, 1, 10, 0)) // 2025-12-01 10:00
                        .repeatInterval(RepeatInterval.WEEKLY)
                        .lastExecutedAt(null)
                        .createdAt(LocalDateTime.now())
                        .updatedAt(LocalDateTime.now())
                        .build()
        );

        given(scheduleService.findAll(anyLong()))
                .willReturn(mockResponse);

        // when & then
        mockMvc.perform(get("/api/schedule").param("userId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].title").value("주간 팀 미팅"))
                .andExpect(jsonPath("$[1].title").value("매일관리"));

    }

    @DisplayName("일정 단일 조회 성공")
    @Test
    void findById_success() throws Exception {
        // given
        ScheduleDetailResponseDto responseDto = ScheduleDetailResponseDto.builder()
                .id(1L)
                .userId(1L)
                .title("주간 팀 미팅")
                .startTime(LocalDateTime.of(2025, 12, 1, 10, 0))
                .repeatInterval(RepeatInterval.WEEKLY)
                .lastExecutedAt(null)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        given(scheduleService.findById(anyLong(),anyLong()))
                .willReturn(responseDto);

        // when & then
        mockMvc.perform(get("/api/schedule/1").param("userId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("주간 팀 미팅"));
    }

    @DisplayName("일정 조회 실패 - 데이터가 없는 경우 404 error 반환")
    @Test
    void findAll_fail() throws Exception {
        //given
        given(scheduleService.findAll(anyLong()))
                .willThrow(new ContentNotFoundException("해당 일정을 찾을 수 없습니다."));

        // when & then
        mockMvc.perform(get("/api/schedule").param("userId", "2"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.detail").value("해당 일정을 찾을 수 없습니다."));

    }

    @DisplayName("일정 수정 성공")
    @Test
    void update_success() throws Exception {
        // given
        ScheduleUpdateResponseDto updateResponseDto = ScheduleUpdateResponseDto.builder()
                .id(1L)
                .userId(1L)
                .title("수정 된 타이틀")
                .startTime(LocalDateTime.of(2025, 12, 1, 10, 0))
                .repeatInterval(RepeatInterval.WEEKLY)
                .lastExecutedAt(null)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        given(scheduleService.update(anyLong(), anyLong(), any()))
                .willReturn(updateResponseDto);

        String requestBodyJson = String.format("{\"title\": \"수정 된 타이틀\", \"startTime\": \"%s\", \"repeatInterval\": \"%s\"}",
                LocalDateTime.of(2025, 12, 1, 10, 0), RepeatInterval.WEEKLY.name());

        //when & then
        mockMvc.perform(put("/api/schedule/1")
                        .param("userId", "1")
                        .content(requestBodyJson)
                        .contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("수정 된 타이틀"));

    }

    @DisplayName("일정 수정 실패 - 사용자가 다르면 403 FORBIDDEN을 반환한다")
    @Test
    void update_fail() throws Exception {
        // 가짜 요청 본문 (Update DTO)

        String requestBody = "{\"title\": \"수정된 일정\", \"startTime\": \"2025-11-25T10:00:00\", \"repeatInterval\": \"DAILY\"}";

        // Given: Service가 403 예외를 던지도록 설정
        given(scheduleService.update(anyLong(), anyLong(), any()))
                .willThrow(new PermissionDeniedException("접근 권한이 없습니다."));

        // When & Then: PUT 요청 호출 및 응답 검증
        mockMvc.perform(put("/api/schedule/38")
                        .param("userId", "2")
                        .content(requestBody)
                        .contentType("application/json"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.detail").value("접근 권한이 없습니다."));
    }

    @DisplayName("일정 삭제 성공")
    @Test
    void delete_success() throws Exception {
        // given
        given(scheduleService.deleteById(anyLong(), anyLong())).willReturn(1);

        // when && then
        mockMvc.perform(delete("/api/schedule/1").param("userId", "1"))
                .andExpect(status().isNoContent());
        verify(scheduleService).deleteById(1L, 1L);
    }
    @DisplayName("일정 생성 성공")
    @Test
    void insert_success() throws Exception {
        // given
        ScheduleCreateResponseDto createResponseDto = ScheduleCreateResponseDto.builder()
                .id(1L)
                .userId(1L)
                .title("타이틀 최초 생성")
                .startTime(LocalDateTime.of(2025, 12, 1, 10, 0))
                .repeatInterval(RepeatInterval.WEEKLY)
                .lastExecutedAt(null)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        given(scheduleService.insert(any())).willReturn(createResponseDto);

        String requestBodyJson = """
            {
                "id": 1,
                "userId": 1,
                "title": "타이틀 최초 생성",
                "startTime": "2025-12-01T10:00:00",
                "repeatInterval": "WEEKLY",
                "lastExecutedAt": null,
                "createdAt": "2025-11-21T10:57:35.000000",
                "updatedAt": "2025-11-21T10:57:35.000000"
            }
            """;
        // when && then
        mockMvc.perform(post("/api/schedule")
                        .content(requestBodyJson)
                        .contentType("application/json"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("타이틀 최초 생성"));
    }

}