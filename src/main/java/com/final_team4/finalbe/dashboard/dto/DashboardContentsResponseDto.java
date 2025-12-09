package com.final_team4.finalbe.dashboard.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class DashboardContentsResponseDto {
    private List<DashboardContentItemDto> contents;

    public static DashboardContentsResponseDto from(List<DashboardContentItemDto> items) {

        return DashboardContentsResponseDto.builder().
                contents(items).build();

    }


}
