package com.final_team4.finalbe.schedule.service;

import com.final_team4.finalbe._core.exception.ContentNotFoundException;
import com.final_team4.finalbe.schedule.dto.scheduleSetting.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;


import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Transactional
@SpringBootTest
class ScheduleSettingServiceTest {

    @Autowired
    private ScheduleSettingService scheduleSettingService;

    @Test
    @DisplayName("성공_스케쥴 세팅 등록 테스트")
    void create() {
        // given
        Long userId = 3L;
        ScheduleSettingCreateRequestDto createRequestDto = ScheduleSettingCreateRequestDto.builder()
                .isRun(true)
                .maxDailyRuns(1L)
                .retryOnFail(1L)
                .build();

        // when
        ScheduleSettingCreateResponseDto resultRequestDto = scheduleSettingService.create(userId, createRequestDto);

        // then
        assertThat(resultRequestDto).isNotNull();
        assertThat(resultRequestDto.getId()).isNotNull();
        assertThat(resultRequestDto.isRun()).isTrue();
        assertThat(resultRequestDto.getMaxDailyRuns()).isEqualTo(1);
        assertThat(resultRequestDto.getRetryOnFail()).isEqualTo(1);

    }

    @Test
    @DisplayName("성공_스케쥴 세팅 사용자 별 조회 테스트")
    void findById() {
        // given
        Long userId = 3L;
        ScheduleSettingCreateRequestDto createRequestDto = ScheduleSettingCreateRequestDto.builder()
                .isRun(true)
                .maxDailyRuns(1L)
                .retryOnFail(1L)
                .build();

        ScheduleSettingCreateResponseDto settingCreateResponseDto= scheduleSettingService.create(userId, createRequestDto);

        // when
        ScheduleSettingDetailResponseDto resultRequestDto= scheduleSettingService.findById(userId, settingCreateResponseDto.getId());


        // then
        assertThat(resultRequestDto).isNotNull();
        assertThat(resultRequestDto.getId()).isEqualTo(settingCreateResponseDto.getId());
    }

    @Test
    @DisplayName("성공_스케쥴 업데이트 테스트")
    void update() {
        // given
        Long userId = 3L;
        boolean isRun = false;
        Long retryOnFail = 0L;
        ScheduleSettingCreateRequestDto createRequestDto = ScheduleSettingCreateRequestDto.builder()
                .isRun(true)
                .maxDailyRuns(1L)
                .retryOnFail(1L)
                .build();

        ScheduleSettingUpdateRequestDto updateDto = ScheduleSettingUpdateRequestDto.builder()
                .isRun(isRun)
                .retryOnFail(retryOnFail)
                .maxDailyRuns(1L)
                .build();
        ScheduleSettingCreateResponseDto settingCreateResponseDto= scheduleSettingService.create(userId, createRequestDto);

        // when
        ScheduleSettingUpdateResponseDto resultRequestDto = scheduleSettingService.update(userId, settingCreateResponseDto.getId(), updateDto);

        // then
        assertThat(resultRequestDto).isNotNull();
        assertThat(resultRequestDto.getId()).isEqualTo(settingCreateResponseDto.getId());
        assertThat(resultRequestDto.isRun()).isFalse();
    }

    @DisplayName("실패_존재하지 않는 ID로 조회 시 예외 발생")
    @Test
    void findById_fail_invalidId() {
        // given
        Long userId = 1L;
        Long invalidSettingId = 999L;

        // when & then
        assertThatThrownBy(() -> scheduleSettingService.findById(userId, invalidSettingId))
                .isInstanceOf(ContentNotFoundException.class);
    }

    @DisplayName("실패_존재하지 않는 ID로 업데이트 시 예외 발생")
    @Test
    void update_fail_invalidId() {
        // given
        Long userId = 1L;
        Long invalidSettingId = 999L;
        ScheduleSettingUpdateRequestDto updateDto = ScheduleSettingUpdateRequestDto.builder()
                .isRun(false)
                .retryOnFail(0L)
                .maxDailyRuns(1L)
                .build();

        // when & then
        assertThatThrownBy(() -> scheduleSettingService.update(userId, invalidSettingId, updateDto))
                .isInstanceOf(ContentNotFoundException.class);
    }
}