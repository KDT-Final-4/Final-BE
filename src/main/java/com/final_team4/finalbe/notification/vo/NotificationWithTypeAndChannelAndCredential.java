package com.final_team4.finalbe.notification.vo;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class NotificationWithTypeAndChannelAndCredential {
    private Long id;
    private Long channelId;
    private String channelName;
    private Long userId;
    private Long typeId;
    private String typeDescription;
    private String typeName;
    private String credentialApiToken;
    private String credentialWebhook;
    private Long contentId;
    private String title;
    private String message;
    private Long notificationLevel;
    private LocalDateTime createdAt;
}
