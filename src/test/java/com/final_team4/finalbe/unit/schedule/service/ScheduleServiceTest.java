package com.final_team4.finalbe.unit.schedule.service;

import com.final_team4.finalbe.schedule.domain.Schedule;
import com.final_team4.finalbe.schedule.dto.ScheduleDetailResponseDto;
import com.final_team4.finalbe.schedule.mapper.ScheduleMapper;
import com.final_team4.finalbe.schedule.service.ScheduleService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
class ScheduleServiceTest {

    @InjectMocks
    private ScheduleService scheduleService;

    @Mock
    private ScheduleMapper scheduleMapper;

    @Test
    void findAll() {
        // Given
        Schedule schedule1 = Schedule.builder()
                .id(1L)
                .userId(100L)
                .title("test")
                .startTime(LocalDateTime.now())
                .repeatInterval("DAILY")
                .lastExecutedAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        Schedule schedule2 = Schedule.builder()
                .id(2L)
                .userId(101L)
                .title("test2")
                .startTime(LocalDateTime.now().plusDays(1))
                .repeatInterval("WEEKLY")
                .lastExecutedAt(LocalDateTime.now().plusDays(1))
                .updatedAt(LocalDateTime.now().plusDays(1))
                .build();
        List<Schedule> mockSchedules = Arrays.asList(schedule1, schedule2);

        when(scheduleMapper.findAll()).thenReturn(mockSchedules);

        // When
        List<ScheduleDetailResponseDto> result = scheduleService.findAll();

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(schedule1.getId(), result.get(0).getId());
        assertEquals(schedule2.getId(), result.get(1).getId());

        verify(scheduleMapper).findAll();
    }
}