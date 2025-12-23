package com.final_team4.finalbe.dashboard.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class DashboardContentsCountResponseDto {

    private long contentsCount;

    public static DashboardContentsCountResponseDto of(long contentsCount) {
        return DashboardContentsCountResponseDto.builder()
                .contentsCount(contentsCount)
                .build();
    }

}
