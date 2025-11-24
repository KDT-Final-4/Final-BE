package com.final_team4.finalbe.trend.dto;

import com.final_team4.finalbe.uploadChannel.dto.UploadChannelItemPayload;
import lombok.*;

import java.util.List;

@Getter
@Builder
public class TrendCreateContentPayload {
    private final Long userId;
    private final String keyword;
    private final List<UploadChannelItemPayload> uploadChannels;

    public static TrendCreateContentPayload of(Long userId,
                                               String keyword,
                                               List<UploadChannelItemPayload> uploadChannels) {
        return TrendCreateContentPayload.builder()
                .userId(userId)
                .keyword(keyword)
                .uploadChannels(uploadChannels)
                .build();
    }
}
