package com.final_team4.finalbe.schedule.dto.scheduleSetting;

import com.final_team4.finalbe.schedule.domain.ScheduleSetting;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ScheduleSettingCreateResponseDto {
    @NotNull
    private Long id;

    private boolean isRun;

    @NotNull
    private Long maxDailyRuns;

    @NotNull
    private Long retryOnFail;

    @NotNull
    private LocalDateTime createdAt;

    public static ScheduleSettingCreateResponseDto from(ScheduleSetting entity) {
        return ScheduleSettingCreateResponseDto.builder()
                .id(entity.getId())
                .isRun(entity.isRun())
                .maxDailyRuns(entity.getMaxDailyRuns())
                .retryOnFail(entity.getRetryOnFail())
                .createdAt(entity.getCreatedAt())
                .build();
    }
}
