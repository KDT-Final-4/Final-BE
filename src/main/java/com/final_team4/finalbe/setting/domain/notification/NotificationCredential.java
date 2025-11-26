package com.final_team4.finalbe.setting.domain.notification;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class NotificationCredential {
    private Long id;
    private Long userId;
    private Long channelId;
    private String webhookUrl;
    private String apiToken;
    private boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public void update(String webhookUrl, String apiToken, boolean isActive) {
        this.webhookUrl = webhookUrl;
        this.apiToken = apiToken;
        this.isActive = isActive;
        this.updatedAt = LocalDateTime.now();
    }

}

