package com.final_team4.finalbe.notification.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class NotificationCreateRequestDto {
    @NotNull
    private Long channelId;

    @NotNull
    private Long typeId;

    @NotNull
    private Long contentId;

    @NotBlank
    @Size(min = 1, max = 50)
    private String title;

    private String message;

    @NotNull
    private Long notificationLevel;
}
