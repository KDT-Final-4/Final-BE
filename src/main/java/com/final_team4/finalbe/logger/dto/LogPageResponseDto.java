package com.final_team4.finalbe.logger.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class LogPageResponseDto {
    private final List<LogResponseDto> items;
    private final long totalCount;
    private final int page;
    private final int size;
    // 필요하면 totalPages = (totalCount + size - 1) / size 계산 필드 추가
    public static LogPageResponseDto of(List<LogResponseDto> items, long totalCount, int page, int size) {
        return LogPageResponseDto.builder()
                .items(items)
                .totalCount(totalCount)
                .page(page)
                .size(size)
                .build();
    }
}