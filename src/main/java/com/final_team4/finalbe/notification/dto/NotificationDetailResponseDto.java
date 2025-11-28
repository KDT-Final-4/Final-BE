package com.final_team4.finalbe.notification.dto;

import com.final_team4.finalbe.notification.domain.Notification;
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

    public static NotificationDetailResponseDto from(Notification notification) {
        return NotificationDetailResponseDto.builder()
                .id(notification.getId())
                .channelId(notification.getChannelId())
                .typeId(notification.getTypeId())
                .contentId(notification.getContentId())
                .title(notification.getTitle())
                .message(notification.getMessage())
                .notificationLevel(notification.getNotificationLevel())
                .createdAt(notification.getCreatedAt())
                .build();
    }
}
