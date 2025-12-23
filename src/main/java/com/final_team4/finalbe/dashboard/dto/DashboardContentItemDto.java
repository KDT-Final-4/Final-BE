package com.final_team4.finalbe.dashboard.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonFormat;

@Getter
@Builder
public class DashboardContentItemDto {
    private Long contentId;
    private String title;
    private String keyword;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'", timezone = "Asia/Seoul")
    private LocalDateTime createdAt;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'", timezone = "Asia/Seoul")
    private LocalDateTime updatedAt;
    private String link;
    private long clickCount;
    private Long categoryId;
    private String categoryName;
    private String categoryDescription;

    public static DashboardContentItemDto from(DashboardContentSummary summary) {
        return DashboardContentItemDto.builder()
                .contentId(summary.getContentId())
                .title(summary.getTitle())
                .keyword(summary.getKeyword())
                .createdAt(summary.getCreatedAt())
                .updatedAt(summary.getUpdatedAt())
                .link(summary.getLink())
                .clickCount(summary.getClickCount())
                .categoryId(summary.getCategoryId())
                .categoryName(summary.getCategoryName())
                .categoryDescription(summary.getCategoryDescription())
                .build();
    }

}
