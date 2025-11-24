package com.final_team4.finalbe.schedule.dto.scheduleSetting;

import com.final_team4.finalbe.schedule.domain.ScheduleSetting;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ScheduleSettingDetailResponseDto {
    @NotNull
    private Long id;

    @NotNull
    private Long userId;

    private boolean isRun;

    @NotNull
    private Long maxDailyRuns;

    @NotNull
    private Long retryOnFail;

    @NotNull
    @CreatedDate
    private LocalDateTime createdAt;

    @NotNull
    private LocalDateTime updatedAt;

    public static ScheduleSettingDetailResponseDto from(ScheduleSetting entity) {
       return ScheduleSettingDetailResponseDto.builder()
               .id(entity.getId())
               .userId(entity.getUserId())
               .isRun(entity.isRun())
               .maxDailyRuns(entity.getMaxDailyRuns())
               .retryOnFail(entity.getRetryOnFail())
               .createdAt(entity.getCreatedAt())
               .updatedAt(entity.getUpdatedAt())
               .build();
    }
}
