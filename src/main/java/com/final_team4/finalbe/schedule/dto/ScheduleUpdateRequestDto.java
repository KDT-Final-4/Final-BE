package com.final_team4.finalbe.schedule.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ScheduleUpdateRequestDto {
    private String title;
    private LocalDateTime startTime;
    private String repeatInterval;
    private LocalDateTime lastExecutedAt;
    private LocalDateTime updatedAt;
}
