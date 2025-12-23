package com.final_team4.finalbe.schedule.service;

import com.final_team4.finalbe._core.exception.ContentNotFoundException;
import com.final_team4.finalbe.schedule.domain.RepeatInterval;
import com.final_team4.finalbe.schedule.dto.schedule.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

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
                .title("test")
                .startTime(LocalDateTime.now())
                .repeatInterval(RepeatInterval.DAILY)
                .build();
        // when
        ScheduleCreateResponseDto resultDto = scheduleService.insert(1L, dto);
        // then
        assertThat(resultDto.getTitle()).isEqualTo(dto.getTitle());
        assertThat(resultDto.getRepeatInterval()).isEqualTo(RepeatInterval.DAILY);
    }


    @DisplayName("전체 찾아보기_성공")
    @Test
    void findAll() {
        // Given
        String title = "test";
        String title2 = "test2";
        Long userId = 1L;
        scheduleService.insert( userId, ScheduleCreateRequestDto.builder()
                .title(title)
                .startTime(LocalDateTime.now())
                .repeatInterval(RepeatInterval.DAILY)
                .build());
        scheduleService.insert(userId, ScheduleCreateRequestDto.builder()
                .title(title2)
                .startTime(LocalDateTime.now())
                .repeatInterval(RepeatInterval.DAILY)
                .build());

        // When
        List<ScheduleDetailResponseDto> entities = scheduleService.findAll(userId);

        // Then
        assertThat(entities).hasSize(entities.size());
    }

    @DisplayName("단일 찾아보기_성공")
    @Test
    void findById() {
        // given
        Long userId = 1L;
        ScheduleCreateRequestDto dto = ScheduleCreateRequestDto.builder()
                .title("test")
                .startTime(LocalDateTime.now())
                .repeatInterval(RepeatInterval.DAILY)
                .build();
        ScheduleCreateResponseDto saveDto = scheduleService.insert(userId, dto);

        // when
        ScheduleDetailResponseDto responseDto = scheduleService.findById(userId, saveDto.getId());

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
        Long userId = 1L;

        ScheduleCreateRequestDto origin = ScheduleCreateRequestDto.builder()
                .title("test")
                .startTime(startTime)
                .repeatInterval(RepeatInterval.DAILY)
                .build();
        ScheduleCreateResponseDto originDto = scheduleService.insert(userId, origin);

        ScheduleUpdateRequestDto updateRequestDto = ScheduleUpdateRequestDto.builder()
                .title(updateTitle)
                .repeatInterval(updateRepeatInterval)
                .build();

        // when
        ScheduleUpdateResponseDto updateDto = scheduleService.update(userId, originDto.getId(), updateRequestDto);

        // then
        assertThat(updateDto.getTitle()).isEqualTo(updateTitle);
        assertThat(updateDto.getRepeatInterval()).isEqualTo(updateRepeatInterval);
        assertThat(originDto.getStartTime()).isEqualTo(startTime);
    }

    @DisplayName("성공_스케쥴러 삭제 테스트")
    @Test
    void delete() {
        // given
        Long userId = 1L;
        ScheduleCreateRequestDto dto = ScheduleCreateRequestDto.builder()
                .title("test")
                .startTime(LocalDateTime.now())
                .repeatInterval(RepeatInterval.DAILY)
                .build();
        ScheduleCreateResponseDto saveDto = scheduleService.insert(userId, dto);
        long id = saveDto.getId();

        // when
        scheduleService.deleteById(userId, id);

    }

    // 실패 테스트
    @DisplayName("실패_컨텐츠가 없으면 예외가 발생한다.")
    @Test
    void findAllFail() {
        // Given
        String title = "test";
        String title2 = "test2";
        Long userId = 1L;
        scheduleService.insert(userId,  ScheduleCreateRequestDto.builder()
                .title(title)
                .startTime(LocalDateTime.now())
                .repeatInterval(RepeatInterval.DAILY)
                .build());
        scheduleService.insert(userId,  ScheduleCreateRequestDto.builder()
                .title(title2)
                .startTime(LocalDateTime.now())
                .repeatInterval(RepeatInterval.DAILY)
                .build());

        // when && Then
        assertThatThrownBy(() -> scheduleService.findAll(5L))
                    .isInstanceOf(ContentNotFoundException.class);
    }
}