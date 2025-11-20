package com.final_team4.finalbe.schedule.dto;

import com.final_team4.finalbe.schedule.domain.RepeatInterval;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ScheduleUpdateRequestDto {
    private String title;
    private LocalDateTime startTime;
    private RepeatInterval repeatInterval;

}
