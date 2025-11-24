package com.final_team4.finalbe.schedule.domain;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ScheduleSetting {
    private Long id;
    private Long userId;
    private boolean isRun;
    private Long maxDailyRuns;
    private Long retryOnFail;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public void update(boolean isRun, Long maxDailyRuns, Long retryOnFail) {
        this.isRun = isRun;
        this.maxDailyRuns = maxDailyRuns;
        this.retryOnFail = retryOnFail;
        this.updatedAt = LocalDateTime.now();
    }
}
