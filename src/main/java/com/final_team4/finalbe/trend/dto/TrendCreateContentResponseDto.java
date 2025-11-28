package com.final_team4.finalbe.trend.dto;

import lombok.*;

@Getter
@Builder
public class TrendCreateContentResponseDto {
    private final String keyword;
    private final boolean requested;

    public static TrendCreateContentResponseDto of(String keyword, boolean requested) {
        return TrendCreateContentResponseDto.builder()
                .keyword(keyword)
                .requested(requested)
                .build();
    }
}
