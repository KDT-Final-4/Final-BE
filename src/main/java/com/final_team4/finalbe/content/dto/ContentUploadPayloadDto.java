package com.final_team4.finalbe.content.dto;

import com.final_team4.finalbe.content.domain.Content;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED, force = true)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ContentUploadPayloadDto {

    @NotNull
    private final Long contentId;

    @NotNull
    private final Long userId;

    @NotNull
    private final Long uploadChannelId;

    @NotNull
    private final String jobId;

    @NotNull
    private final String title;

    @NotNull
    private final String body;

    private final String link;

    private final String keyword;

    public static ContentUploadPayloadDto from(Content content) {
        return ContentUploadPayloadDto.builder()
                .contentId(content.getId())
                .userId(content.getUserId())
                .uploadChannelId(content.getUploadChannelId())
                .jobId(content.getJobId())
                .title(content.getTitle())
                .body(content.getBody())
                .link(content.getLink())
                .keyword(content.getKeyword())
                .build();
    }
}
