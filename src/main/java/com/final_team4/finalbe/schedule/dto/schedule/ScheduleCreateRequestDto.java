package com.final_team4.finalbe.schedule.dto.schedule;

import com.final_team4.finalbe.schedule.domain.RepeatInterval;
import com.final_team4.finalbe.schedule.domain.Schedule;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ScheduleCreateRequestDto {

    @NotBlank
    @Size(min = 1, max = 50)
    private String title;

    @NotNull
    private LocalDateTime startTime;

    @NotNull
    private RepeatInterval repeatInterval;

    public Schedule toEntity(Long userId) {
       return Schedule.builder()
               .userId(userId)
               .title(title)
               .startTime(startTime)
               .repeatInterval(repeatInterval)
               .build();
    }
}
