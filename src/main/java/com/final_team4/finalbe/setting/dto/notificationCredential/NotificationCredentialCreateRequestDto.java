package com.final_team4.finalbe.setting.dto.notificationCredential;

import com.final_team4.finalbe.setting.domain.notification.NotificationCredential;
import lombok.Builder;
import lombok.Getter;


@Getter
@Builder
public class NotificationCredentialCreateRequestDto {
    private Long channelId;
    private String webhookUrl;
    private String apiToken;
    private boolean isActive;

    public NotificationCredential toEntity(Long userId) {
        return NotificationCredential.builder()
                .userId(userId)
                .channelId(channelId)
                .webhookUrl(webhookUrl)
                .apiToken(apiToken)
                .isActive(isActive)
                .build();
    }
}
