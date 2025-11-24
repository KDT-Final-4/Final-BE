package com.final_team4.finalbe.schedule.dto.scheduleSetting;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ScheduleSettingUpdateRequestDto {

    @NotNull
    private boolean isRun;

    @NotNull
    private Long maxDailyRuns;

    @NotNull
    private Long retryOnFail;
}
