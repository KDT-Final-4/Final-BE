package com.final_team4.finalbe.setting.dto.notification;

import com.final_team4.finalbe.setting.domain.notification.NotificationCredential;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class NotificationCredentialCreateResponseDto {
    private Long id;
    private Long channelId;
    private Boolean isActive;

    public static NotificationCredentialCreateResponseDto from(NotificationCredential notificationCredential) {
        return NotificationCredentialCreateResponseDto.builder()
                .id(notificationCredential.getId())
                .channelId(notificationCredential.getChannelId())
                .isActive(notificationCredential.getIsActive())
                .build();
    }
}
