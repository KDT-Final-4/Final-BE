package com.final_team4.finalbe.notification.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class NotificationDetailResponseDto {
    private Long id;
    private Long channelId;
    private Long typeId;
    private Long contentId;
    private String title;
    private String message;
    private Long notificationLevel;
    private LocalDateTime createdAt;
}
