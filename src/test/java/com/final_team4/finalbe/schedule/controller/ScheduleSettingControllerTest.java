package com.final_team4.finalbe.schedule.controller;

import com.final_team4.finalbe.schedule.dto.scheduleSetting.ScheduleSettingDetailResponseDto;
import com.final_team4.finalbe.schedule.dto.scheduleSetting.ScheduleSettingUpdateRequestDto;
import com.final_team4.finalbe.schedule.dto.scheduleSetting.ScheduleSettingUpdateResponseDto;
import com.final_team4.finalbe.schedule.service.ScheduleSettingService;
import java.time.LocalDateTime;

import com.final_team4.finalbe.user.domain.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mybatis.spring.boot.autoconfigure.MybatisAutoConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(
        controllers = ScheduleSettingController.class,
        excludeAutoConfiguration = {
                SecurityAutoConfiguration.class,
                MybatisAutoConfiguration.class
        }
)
class ScheduleSettingControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    ScheduleSettingService scheduleSettingService;

    @Test
    @DisplayName("스케줄 설정 수정 성공 시 200 OK와 수정된 정보 반환")
    void update_success() throws Exception {
        // given
        ScheduleSettingUpdateResponseDto responseDto = ScheduleSettingUpdateResponseDto.builder()
                .id(10L)
                .isRun(true)
                .maxDailyRuns(5L)
                .retryOnFail(2L)
                .updatedAt(LocalDateTime.now())
                .build();

        given(scheduleSettingService.update(anyLong(), anyLong(), any()))
                .willReturn(responseDto);

        String requestBody = """
                {
                  "isRun": true,
                  "maxDailyRuns": 5,
                  "retryOnFail": 2
                }
                """;

        // when & then
        mockMvc.perform(put("/api/setting/schedule/10")
                        .param("userId", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(10))
                .andExpect(jsonPath("$.maxDailyRuns").value(5))
                .andExpect(jsonPath("$.retryOnFail").value(2));

        verify(scheduleSettingService).update(eq(1L), eq(10L), any(ScheduleSettingUpdateRequestDto.class));
    }

    //Long userId 로는 성공했지만, UserDetail이 아직 생성이 안되어 현재 테스트 코드는 오류.
    @Test
    @DisplayName("스케줄 설정 단건 조회 성공 시 200 OK와 상세 정보 반환")
    void findById_success() throws Exception {
        // given
        User mockUser = User.builder()
                .id(1L)
                .email("test@user.com")
                .name("testUser")
                .build();
        ScheduleSettingDetailResponseDto detailResponse = ScheduleSettingDetailResponseDto.builder()
                .id(3L)
                .userId(1L)
                .isRun(false)
                .maxDailyRuns(1L)
                .retryOnFail(0L)
                .createdAt(LocalDateTime.now().minusDays(1))
                .updatedAt(LocalDateTime.now())
                .build();

        given(scheduleSettingService.findById(anyLong(), anyLong()))
                .willReturn(detailResponse);

        // when & then
        mockMvc.perform(get("/api/setting/schedule/3")
//                        .with(user((UserDetails) mockUser))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(3))
                .andExpect(jsonPath("$.userId").value(1))
                .andExpect(jsonPath("$.maxDailyRuns").value(1))
                .andExpect(jsonPath("$.retryOnFail").value(0));

        verify(scheduleSettingService).findById(1L, 3L);
    }

}
