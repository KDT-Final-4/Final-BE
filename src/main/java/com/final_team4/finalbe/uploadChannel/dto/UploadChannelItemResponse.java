package com.final_team4.finalbe.uploadChannel.dto;

import com.final_team4.finalbe.uploadChannel.domain.UploadChannel;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class UploadChannelItemResponse {
    private Long id;
    private Long userId;
    private String name;
    private String apiKey;
    private Boolean status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static UploadChannelItemResponse from(UploadChannel channel) {
        return UploadChannelItemResponse.builder()
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
