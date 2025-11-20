package com.final_team4.finalbe.schedule.dto;

import com.final_team4.finalbe.schedule.domain.Schedule;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ScheduleCreateRequestDto {
    private Long userId;
    private String title;
    private LocalDateTime startTime;
    private String repeatInterval;
    private LocalDateTime lastExecutedAt;
    private LocalDateTime createdAt;

    public Schedule toEntity() {
       return Schedule.builder()
               .userId(userId)
               .title(title)
               .startTime(startTime)
               .repeatInterval(repeatInterval)
               .lastExecutedAt(lastExecutedAt)
               .createdAt(createdAt)
               .build();
    }
}
