package com.final_team4.finalbe.trend.dto;

import com.final_team4.finalbe.trend.domain.Trend;
import com.final_team4.finalbe.trend.domain.TrendSnsType;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class TrendResponseDto {
    private Long id;
    private Long categoryId;
    private String keyword;
    private Long searchVolume;
    private LocalDateTime createdAt;
    private TrendSnsType snsType;

    public static TrendResponseDto from(Trend trend) {
        return TrendResponseDto.builder()
                .id(trend.getId())
                .categoryId(trend.getCategoryId())
                .keyword(trend.getKeyword())
                .searchVolume(trend.getSearchVolume())
                .createdAt(trend.getCreatedAt())
                .snsType(trend.getSnsType())
                .build();
    }
}
