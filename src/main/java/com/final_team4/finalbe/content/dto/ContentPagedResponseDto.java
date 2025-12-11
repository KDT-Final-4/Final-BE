package com.final_team4.finalbe.content.dto;

import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ContentPagedResponseDto {
    private final List<ContentListResponseDto> items;
    private final long totalCount;
    private final int page;
    private final int size;

    public static ContentPagedResponseDto of(List<ContentListResponseDto> items, long totalCount, int page, int size) {
        return ContentPagedResponseDto.builder()
                .items(items)
                .totalCount(totalCount)
                .page(page)
                .size(size)
                .build();
    }
}
