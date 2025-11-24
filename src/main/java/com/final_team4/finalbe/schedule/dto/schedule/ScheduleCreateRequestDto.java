package com.final_team4.finalbe.schedule.dto.schedule;

import com.final_team4.finalbe.schedule.domain.RepeatInterval;
import com.final_team4.finalbe.schedule.domain.Schedule;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ScheduleCreateRequestDto {
    @NotNull
    private Long userId;
    @NotNull
    private String title;
    @NotNull
    private LocalDateTime startTime;
    @NotNull
    private RepeatInterval repeatInterval;

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
