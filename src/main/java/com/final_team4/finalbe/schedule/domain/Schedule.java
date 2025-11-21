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
    private RepeatInterval repeatInterval;
    private LocalDateTime lastExecutedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public void update(String title, LocalDateTime startTime, RepeatInterval repeatInterval) {
        if (title != null) this.title = title;
        if (startTime != null) this.startTime = startTime;
        if (repeatInterval != null) this.repeatInterval = repeatInterval;
        this.updatedAt = LocalDateTime.now();
    }
}
