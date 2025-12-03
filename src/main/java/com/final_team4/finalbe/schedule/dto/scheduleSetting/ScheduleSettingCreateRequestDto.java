package com.final_team4.finalbe.schedule.dto.scheduleSetting;

import com.final_team4.finalbe.schedule.domain.ScheduleSetting;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class ScheduleSettingCreateRequestDto {
    private boolean isRun;

    @NotNull
    private Long maxDailyRuns;

    @NotNull
    private Long retryOnFail;

    public ScheduleSetting toEntity(Long userId) {
        return ScheduleSetting.builder()
                .userId(userId)
                .isRun(this.isRun)
                .maxDailyRuns(this.maxDailyRuns)
                .retryOnFail(this.retryOnFail)
                .updatedAt(LocalDateTime.now())
                .build();
    }

}
