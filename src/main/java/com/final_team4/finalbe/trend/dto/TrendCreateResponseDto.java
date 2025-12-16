package com.final_team4.finalbe.trend.dto;

import com.final_team4.finalbe.trend.domain.Trend;
import com.final_team4.finalbe.trend.domain.TrendSnsType;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonFormat;

@Getter
@Builder
public class TrendCreateResponseDto {
    private Long id;
    private Long categoryId;
    private String keyword;
    private Long searchVolume;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'", timezone = "Asia/Seoul")
    private LocalDateTime createdAt;
    private TrendSnsType snsType;

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
