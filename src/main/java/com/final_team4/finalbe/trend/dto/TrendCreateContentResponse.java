package com.final_team4.finalbe.trend.dto;

import lombok.*;

@Getter
@Builder
public class TrendCreateContentResponse {
    private final String keyword;
    private final boolean requested;

    public static TrendCreateContentResponse of(String keyword, boolean requested) {
        return TrendCreateContentResponse.builder()
                .keyword(keyword)
                .requested(requested)
                .build();
    }
}
