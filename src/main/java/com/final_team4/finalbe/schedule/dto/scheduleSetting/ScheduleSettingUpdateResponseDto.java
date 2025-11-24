package com.final_team4.finalbe.schedule.dto.scheduleSetting;

import com.final_team4.finalbe.schedule.domain.ScheduleSetting;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ScheduleSettingUpdateResponseDto {
    @NotNull
    private Long id;
    @NotNull
    private boolean isRun;
    @NotNull
    private Long maxDailyRuns;
    @NotNull
    private Long retryOnFail;
    @NotNull
    private LocalDateTime updatedAt;

    public static ScheduleSettingUpdateResponseDto from(ScheduleSetting scheduleSetting) {
        return ScheduleSettingUpdateResponseDto.builder()
                .id(scheduleSetting.getId())
                .isRun(scheduleSetting.isRun())
                .maxDailyRuns(scheduleSetting.getMaxDailyRuns())
                .retryOnFail(scheduleSetting.getRetryOnFail())
                .updatedAt(scheduleSetting.getUpdatedAt())
                .build();
    }
}
