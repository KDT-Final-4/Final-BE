package com.final_team4.finalbe.dashboard.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class DashboardStatusGetResponseDto {

    private long allClicks;

    private long allViews;

    private long visitors;

    private long averageDwellTime; // seconds


}
