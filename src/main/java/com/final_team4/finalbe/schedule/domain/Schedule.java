package com.final_team4.finalbe.schedule.domain;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Schedule {
    private final Long id;
    private final Long userId;
    private final String title;
    private final LocalDateTime startTime;
    private final String repeatInterval;
    private final LocalDateTime lastExecutedAt;
    private final LocalDateTime updatedAt;
}
