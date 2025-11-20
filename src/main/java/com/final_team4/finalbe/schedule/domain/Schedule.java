package com.final_team4.finalbe.schedule.domain;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Schedule {
    private Long id;
    private Long userId;
    private String title;
    private LocalDateTime startTime;
    private String repeatInterval;
    private LocalDateTime lastExecutedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public void update(String title, LocalDateTime startTime, String repeatInterval, LocalDateTime lastExecutedAt) {
        this.title = title;
        this.startTime = startTime;
        this.repeatInterval = repeatInterval;
        this.lastExecutedAt = lastExecutedAt;
        this.updatedAt = LocalDateTime.now();
    }
}
