package com.final_team4.finalbe.schedule.dto.schedule;

import com.final_team4.finalbe.schedule.domain.RepeatInterval;
import com.final_team4.finalbe.schedule.domain.Schedule;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class ScheduleUpdateResponseDto {
    private Long id;
    private Long userId;
    private String title;
    private LocalDateTime startTime;
    private RepeatInterval repeatInterval;
    private LocalDateTime lastExecutedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static ScheduleUpdateResponseDto from(Schedule schedule) {
        return ScheduleUpdateResponseDto.builder()
                .id(schedule.getId())
                .userId(schedule.getUserId())
                .title(schedule.getTitle())
                .startTime(schedule.getStartTime())
                .repeatInterval(schedule.getRepeatInterval())
                .lastExecutedAt(schedule.getLastExecutedAt())
                .createdAt(schedule.getCreatedAt())
                .updatedAt(schedule.getUpdatedAt())
                .build();
    }
}
