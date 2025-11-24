package com.final_team4.finalbe.trend.dto;

import com.final_team4.finalbe.uploadChannel.dto.UploadChannelItemPayload;
import jakarta.validation.constraints.*;
import lombok.*;

import java.util.List;

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
