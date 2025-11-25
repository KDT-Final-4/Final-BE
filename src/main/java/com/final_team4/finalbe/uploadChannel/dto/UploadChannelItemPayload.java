package com.final_team4.finalbe.uploadChannel.dto;

import com.final_team4.finalbe.uploadChannel.domain.Channel;
import com.final_team4.finalbe.uploadChannel.domain.UploadChannel;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class UploadChannelItemPayload {
    private Long id;
    private Long userId;
    private Channel name;
    private String apiKey;
    private Boolean status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static UploadChannelItemPayload from(UploadChannel channel) {
        return UploadChannelItemPayload.builder()
                .id(channel.getId())
                .userId(channel.getUserId())
                .name(channel.getName())
                .apiKey(channel.getApiKey())
                .status(channel.getStatus())
                .createdAt(channel.getCreatedAt())
                .updatedAt(channel.getUpdatedAt())
                .build();
    }
}
