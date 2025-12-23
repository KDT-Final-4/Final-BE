package com.final_team4.finalbe.notification.domain;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class Notification {
    private Long id;
    private Long channelId;
    private Long userId;
    private Long typeId;
    private String contentJobId;
    private String title;
    private String message;
    private Long notificationLevel;
    private LocalDateTime createdAt;
}
