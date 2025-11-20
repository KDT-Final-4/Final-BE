package com.final_team4.finalbe.schedule.service;

import com.final_team4.finalbe.schedule.domain.RepeatInterval;
import com.final_team4.finalbe.schedule.dto.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class ScheduleServiceTest {

    @Autowired
    private ScheduleService scheduleService;

    @DisplayName("생성 테스트_성공")
    @Test
    void insert() {
        // given
        ScheduleCreateRequestDto dto = ScheduleCreateRequestDto.builder()
                .userId(1L)
                .title("test")
                .startTime(LocalDateTime.now())
                .repeatInterval(RepeatInterval.DAILY)
                .build();
        // when
        ScheduleCreateResponseDto resultDto = scheduleService.insert(dto);
        // then
        assertThat(resultDto.getTitle()).isEqualTo(dto.getTitle());
        assertThat(dto.getRepeatInterval()).isEqualTo(RepeatInterval.DAILY);
    }


    @DisplayName("전체 찾아보기_성공")
    @Test
    void findAll() {
        // Given
        String title = "test";
        String title2 = "test2";
        scheduleService.insert( ScheduleCreateRequestDto.builder()
                .userId(1L)
                .title(title)
                .startTime(LocalDateTime.now())
                .repeatInterval(RepeatInterval.DAILY)
                .build());
        scheduleService.insert( ScheduleCreateRequestDto.builder()
                .userId(1L)
                .title(title2)
                .startTime(LocalDateTime.now())
                .repeatInterval(RepeatInterval.DAILY)
                .build());

        // When
        List<ScheduleDetailResponseDto> entities = scheduleService.findAll(1L);

        // Then
        assertThat(entities).hasSize(2);
        assertThat(entities.getFirst().getTitle()).isEqualTo(title);
        assertThat(entities.get(1).getTitle()).isEqualTo(title2);
    }

    @DisplayName("단일 찾아보기_성공")
    @Test
    void findById() {
        // given
        ScheduleCreateRequestDto dto = ScheduleCreateRequestDto.builder()
                .userId(1L)
                .title("test")
                .startTime(LocalDateTime.now())
                .repeatInterval(RepeatInterval.DAILY)
                .build();
        ScheduleCreateResponseDto saveDto = scheduleService.insert(dto);

        // when
        ScheduleDetailResponseDto responseDto = scheduleService.findById(1L, saveDto.getId());

        // then
        assertThat(saveDto.getId()).isEqualTo(responseDto.getId());
        assertThat(responseDto.getTitle()).isEqualTo(dto.getTitle());
    }

    @DisplayName("업데이트_성공")
    @Test
    void update() { // given
        LocalDateTime startTime = LocalDateTime.now();
        String updateTitle = "test2";
        RepeatInterval updateRepeatInterval = RepeatInterval.MONTHLY;
        ScheduleCreateRequestDto origin = ScheduleCreateRequestDto.builder()
                .userId(1L)
                .title("test")
                .startTime(startTime)
                .repeatInterval(RepeatInterval.DAILY)
                .build();
        ScheduleCreateResponseDto originDto = scheduleService.insert(origin);

        ScheduleUpdateRequestDto updateRequestDto = ScheduleUpdateRequestDto.builder()
                .title(updateTitle)
                .repeatInterval(updateRepeatInterval)
                .build();

        // when
        ScheduleUpdateResponseDto updateDto = scheduleService.update(1L, originDto.getId(), updateRequestDto);

        // then
        assertThat(updateDto.getTitle()).isEqualTo(updateTitle);
        assertThat(updateDto.getRepeatInterval()).isEqualTo(updateRepeatInterval);
        assertThat(originDto.getStartTime()).isEqualTo(startTime);
    }

    @DisplayName("스케쥴러 삭제 테스트_성공")
    @Test
    void delete() {
        // given
        ScheduleCreateRequestDto dto = ScheduleCreateRequestDto.builder()
                .userId(1L)
                .title("test")
                .startTime(LocalDateTime.now())
                .repeatInterval(RepeatInterval.DAILY)
                .build();
        ScheduleCreateResponseDto saveDto = scheduleService.insert(dto);
        long id = saveDto.getId();

        // when
        int result = scheduleService.deleteById(1L, id);

        // then
        assertThat(result).isEqualTo(1);
    }
}