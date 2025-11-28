package com.final_team4.finalbe.trend.dto;

import com.final_team4.finalbe.trend.domain.Trend;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class TrendCreateResponseDto {
    private Long id;
    private Long categoryId;
    private String keyword;
    private Long searchVolume;
    private LocalDateTime createdAt;
    private String snsType;

    public static TrendCreateResponseDto from(Trend trend) {
        return TrendCreateResponseDto.builder()
                .id(trend.getId())
                .categoryId(trend.getCategoryId())
                .keyword(trend.getKeyword())
                .searchVolume(trend.getSearchVolume())
                .createdAt(trend.getCreatedAt())
                .snsType(trend.getSnsType())
                .build();
  }
}
