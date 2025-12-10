package com.final_team4.finalbe.trend.dto;

import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TrendListResponseDto {
    private final List<TrendResponseDto> items;
    private final long totalCount;
    private final int page;
    private final int size;

    public static TrendListResponseDto of(List<TrendResponseDto> items, long totalCount, int page, int size) {
        return TrendListResponseDto.builder()
                .items(items)
                .totalCount(totalCount)
                .page(page)
                .size(size)
                .build();
    }
}
