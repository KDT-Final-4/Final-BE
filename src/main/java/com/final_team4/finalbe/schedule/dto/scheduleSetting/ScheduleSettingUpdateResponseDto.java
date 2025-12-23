package com.final_team4.finalbe.schedule.dto.scheduleSetting;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.final_team4.finalbe.schedule.domain.ScheduleSetting;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonFormat;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ScheduleSettingUpdateResponseDto {
    @NotNull
    private Long id;

    @JsonProperty("isRun")
    @Schema(name = "isRun")
    private boolean isRun;

    @NotNull
    private Long maxDailyRuns;

    @NotNull
    private Long retryOnFail;

    @NotNull
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'", timezone = "Asia/Seoul")
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
