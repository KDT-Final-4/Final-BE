package com.final_team4.finalbe.schedule.domain;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Schedule {
    private Long id;                        // IDENTITY 컬럼
    private Long userId;                    // USER_ID
    private String title;                   // TITLE

    private LocalDateTime startTime;        // START_TIME
    private RepeatInterval repeatInterval;  // REPEAT_INTERVAL (Enum)

    private LocalDateTime nextExecutionAt;  // NEXT_EXECUTION_AT
    private LocalDateTime lastExecutedAt;   // LAST_EXECUTED_AT

    private Boolean isActive;               // IS_ACTIVE (1/0)
    private Boolean isLocked;               // IS_LOCKED (1/0)

    private LocalDateTime createdAt;        // CREATED_AT
    private LocalDateTime updatedAt;        // UPDATED_AT

    public void update(String title, LocalDateTime startTime, RepeatInterval repeatInterval) {
        if (title != null) this.title = title;
        if (startTime != null) this.startTime = startTime;
        if (repeatInterval != null) this.repeatInterval = repeatInterval;
        this.updatedAt = LocalDateTime.now();
    }
}
