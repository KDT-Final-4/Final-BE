package com.final_team4.finalbe.schedule.dto.scheduleSetting;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.final_team4.finalbe.schedule.domain.ScheduleSetting;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class ScheduleSettingCreateRequestDto {

    @JsonProperty("isRun")
    @Schema(name = "isRun")
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
