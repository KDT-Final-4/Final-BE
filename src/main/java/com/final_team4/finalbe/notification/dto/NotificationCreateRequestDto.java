package com.final_team4.finalbe.notification.dto;

import com.final_team4.finalbe.notification.domain.Notification;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class NotificationCreateRequestDto {

    @NotNull
    private Long typeId;

    @NotBlank
    private String contentJobId;

    @NotBlank
    @Size(min = 1, max = 50)
    private String title;

    private String message;

    @NotNull
    private Long notificationLevel;

    public Notification toEntity(Long userId, Long channelId) {
        return Notification.builder()
                .userId(userId)
                .channelId(channelId)
                .typeId(typeId)
                .contentJobId(contentJobId)
                .title(title)
                .message(message)
                .notificationLevel(notificationLevel)
                .build();
    }
}
