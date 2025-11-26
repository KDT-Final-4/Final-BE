package com.final_team4.finalbe.setting.dto.notificationCredential;

import com.final_team4.finalbe.setting.domain.notification.NotificationCredential;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class NotificationCredentialCreateResponseDto {
    private Long id;
    private Long channelId;
    private String webhookUrl;
    private String apiToken;
    private boolean isActive;

    public static NotificationCredentialCreateResponseDto from(NotificationCredential notificationCredential) {
        return NotificationCredentialCreateResponseDto.builder()
                .id(notificationCredential.getId())
                .channelId(notificationCredential.getChannelId())
                .webhookUrl(notificationCredential.getWebhookUrl())
                .apiToken(notificationCredential.getApiToken())
                .isActive(notificationCredential.isActive())
                .build();
    }
}
