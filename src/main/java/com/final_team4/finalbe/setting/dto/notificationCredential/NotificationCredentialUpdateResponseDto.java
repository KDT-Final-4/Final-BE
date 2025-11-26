package com.final_team4.finalbe.setting.dto.notificationCredential;


import com.final_team4.finalbe.setting.domain.notification.NotificationCredential;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class NotificationCredentialUpdateResponseDto {
    private Long id;
    private Long channelId;
    private String webhookUrl;
    private String apiToken;
    private Boolean isActive;


    public static NotificationCredentialUpdateResponseDto from(NotificationCredential entity) {
        return NotificationCredentialUpdateResponseDto.builder()
                .id(entity.getId())
                .channelId(entity.getChannelId())
                .webhookUrl(entity.getWebhookUrl())
                .apiToken(entity.getApiToken())
                .isActive(entity.getIsActive())
                .build();
    }
}
