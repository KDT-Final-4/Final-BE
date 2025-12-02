package com.final_team4.finalbe.dashboard.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class DashboardContentItemDto {
    private Long contentId;
    private String title;
    private String keyword;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String contentLink;
    private long clickCount;

    public static DashboardContentItemDto from(DashboardContentSummary summary, long clickCount) {
        return DashboardContentItemDto.builder()
                .contentId(summary.getContentId())
                .title(summary.getTitle())
                .keyword(summary.getKeyword())
                .createdAt(summary.getCreatedAt())
                .updatedAt(summary.getUpdatedAt())
                .contentLink(summary.getContentLink())
                .clickCount(clickCount)
                .build();
    }

}
