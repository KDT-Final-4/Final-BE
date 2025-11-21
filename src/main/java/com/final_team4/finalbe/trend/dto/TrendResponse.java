package com.final_team4.finalbe.trend.dto;

import com.final_team4.finalbe.trend.domain.Trend;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class TrendResponse {
    private Long id;
    private Long categoryId;
    private String keyword;
    private Long searchVolume;
    private LocalDateTime createdAt;
    private String snsType;

    public static TrendResponse from(Trend trend) {
        return TrendResponse.builder()
                .id(trend.getId())
                .categoryId(trend.getCategoryId())
                .keyword(trend.getKeyword())
                .searchVolume(trend.getSearchVolume())
                .createdAt(trend.getCreatedAt())
                .snsType(trend.getSnsType())
                .build();
    }
}
