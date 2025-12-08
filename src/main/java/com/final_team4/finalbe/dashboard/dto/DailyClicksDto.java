package com.final_team4.finalbe.dashboard.dto;

import lombok.*;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class DailyClicksDto {

    private String date;
    private long clicks;
}
