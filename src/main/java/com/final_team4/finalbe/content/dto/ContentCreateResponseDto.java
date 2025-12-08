package com.final_team4.finalbe.content.dto;

import com.final_team4.finalbe.content.domain.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Builder
public class ContentCreateResponseDto {

    private Long id;
    private String jobId;
    private Long userId;
    private Long uploadChannelId;
    private String title;
    private String body;
    private ContentStatus status;
    private ContentGenType generationType;
    private String link;
    private String keyword;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static ContentCreateResponseDto from(Content content) {
        return ContentCreateResponseDto.builder()
                .id(content.getId())
                .jobId(content.getJobId())
                .userId(content.getUserId())
                .uploadChannelId(content.getUploadChannelId())
                .title(content.getTitle())
                .body(content.getBody())
                .status(content.getStatus())
                .generationType(content.getGenerationType())
                .link(content.getLink())
                .keyword(content.getKeyword())
                .createdAt(content.getCreatedAt())
                .updatedAt(content.getUpdatedAt())
                .build();
    }

}
