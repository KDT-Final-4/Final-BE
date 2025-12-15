package com.final_team4.finalbe.setting.dto.notification;

import com.final_team4.finalbe.setting.domain.notification.NotificationCredential;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonFormat;

@Getter
@Builder
public class NotificationCredentialDetailResponseDto {
    private Long id;
    private Long userId;
    private Long channelId;
    private String webhookUrl;
    private String apiToken;
    private Boolean isActive;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'", timezone = "Asia/Seoul")
    private LocalDateTime createdAt;

    public static NotificationCredentialDetailResponseDto from(NotificationCredential entity) {
        return NotificationCredentialDetailResponseDto.builder()
                .id(entity.getId())
                .userId(entity.getUserId())
                .channelId(entity.getChannelId())
                .webhookUrl(entity.getWebhookUrl())
                .apiToken(entity.getApiToken())
                .isActive(entity.getIsActive())
                .createdAt(entity.getCreatedAt())
                .build();
    }

}
