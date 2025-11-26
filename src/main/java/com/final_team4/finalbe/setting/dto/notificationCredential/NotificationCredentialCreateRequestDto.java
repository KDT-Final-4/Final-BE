package com.final_team4.finalbe.setting.dto.notificationCredential;

import com.final_team4.finalbe.setting.domain.notification.NotificationCredential;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;


@Getter
@Builder
public class NotificationCredentialCreateRequestDto {
    @NotNull
    private Long channelId;
    @Size(max = 512)
    private String webhookUrl;
    @Size(max = 256)
    private String apiToken;
    @NotNull
    private Boolean isActive;

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
