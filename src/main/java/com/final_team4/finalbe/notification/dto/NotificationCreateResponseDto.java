package com.final_team4.finalbe.notification.dto;

import com.final_team4.finalbe.notification.domain.Notification;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class NotificationCreateResponseDto {
    private Long id;
    private Long channelId;
    private Long typeId;
    private Long contentId;
    private String title;
    private String message;
    private Long notificationLevel;
    private LocalDateTime createdAt;

    public static NotificationCreateResponseDto from(Notification entity) {
        return NotificationCreateResponseDto.builder()
                .id(entity.getId())
                .channelId(entity.getChannelId())
                .typeId(entity.getTypeId())
                .contentId(entity.getContentId())
                .title(entity.getTitle())
                .message(entity.getMessage())
                .notificationLevel(entity.getNotificationLevel())
                .createdAt(entity.getCreatedAt())
                .build();
    }
}
