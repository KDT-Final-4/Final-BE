package com.final_team4.finalbe.trend.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TrendCreateRequest {

    private Long categoryId;

    private String keyword;

    private Long searchVolume;

    private LocalDateTime createdAt;

    private String snsType;
}
