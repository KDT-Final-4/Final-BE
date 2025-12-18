package com.final_team4.finalbe.notification.dto;

import com.final_team4.finalbe.notification.domain.Notification;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonFormat;

@Getter
@Builder
public class NotificationDetailResponseDto {
    private Long id;
    private Long channelId;
    private Long typeId;
    private String contentJobId;
    private String title;
    private String message;
    private Long notificationLevel;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'", timezone = "Asia/Seoul")
    private LocalDateTime createdAt;

    public static NotificationDetailResponseDto from(Notification notification) {
        return NotificationDetailResponseDto.builder()
                .id(notification.getId())
                .channelId(notification.getChannelId())
                .typeId(notification.getTypeId())
                .contentJobId(notification.getContentJobId())
                .title(notification.getTitle())
                .message(notification.getMessage())
                .notificationLevel(notification.getNotificationLevel())
                .createdAt(notification.getCreatedAt())
                .build();
    }
}
