package com.final_team4.finalbe.trend.dto;

import com.final_team4.finalbe.uploadChannel.dto.UploadChannelItemPayload;
import jakarta.validation.constraints.*;
import lombok.*;

import java.util.List;
import java.util.UUID;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED, force = true)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class TrendCreateContentPayload {
    @NotNull
    private final Long userId;

    @NotBlank
    private final String keyword;

    @NotEmpty
    private final List<UploadChannelItemPayload> uploadChannels;

    @NotNull
    private final UUID jobId;

    public static TrendCreateContentPayload of(Long userId,
                                               String keyword,
                                               List<UploadChannelItemPayload> uploadChannels,
                                               UUID jobId) {
        return TrendCreateContentPayload.builder()
                .userId(userId)
                .keyword(keyword)
                .uploadChannels(uploadChannels)
                .jobId(jobId)
                .build();
    }
}
