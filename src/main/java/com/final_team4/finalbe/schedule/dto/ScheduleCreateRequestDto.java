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

    public Schedule toEntity() {
       return Schedule.builder()
               .userId(userId)
               .title(title)
               .startTime(startTime)
               .repeatInterval(repeatInterval)
               .createdAt(LocalDateTime.now())
               .build();
    }
}
