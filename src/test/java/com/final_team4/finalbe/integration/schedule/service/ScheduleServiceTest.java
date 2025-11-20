package com.final_team4.finalbe.integration.schedule.service;

import com.final_team4.finalbe.schedule.dto.ScheduleDetailResponseDto;
import com.final_team4.finalbe.schedule.service.ScheduleService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class ScheduleServiceTest {

    @Autowired
    private ScheduleService scheduleService;

    @DisplayName("전체 찾아보기")
    @Test
    void findAll() {
        // Given

        // When
        List<ScheduleDetailResponseDto> entities = scheduleService.findAll();

        // Then
        assertThat(entities).hasSize(1);
    }
}