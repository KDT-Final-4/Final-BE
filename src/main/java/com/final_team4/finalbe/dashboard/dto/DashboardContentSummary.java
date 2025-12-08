package com.final_team4.finalbe.dashboard.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class DashboardContentSummary {

    private Long contentId;
    private String title;
    private String keyword;
    private String link;
    private long clickCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
