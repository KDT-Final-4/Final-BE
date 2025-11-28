package com.final_team4.finalbe.content.dto;

import com.final_team4.finalbe.content.domain.Content;
import com.final_team4.finalbe.content.domain.ContentGenType;
import com.final_team4.finalbe.content.domain.ContentStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class ContentUpdateResponseDto {

    private Long id;
    private String jobId;
    private Long userId;
    private Long uploadChannelId;
    private String title;
    private String body;
    private ContentStatus status;
    private ContentGenType generationType;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static ContentUpdateResponseDto from(Content content) {
        return ContentUpdateResponseDto.builder()
                .id(content.getId())
                .jobId(content.getJobId())
                .userId(content.getUserId())
                .uploadChannelId(content.getUploadChannelId())
                .title(content.getTitle())
                .body(content.getBody())
                .status(content.getStatus())
                .generationType(content.getGenerationType())
                .createdAt(content.getCreatedAt())
                .updatedAt(content.getUpdatedAt())
                .build();

    }
}
