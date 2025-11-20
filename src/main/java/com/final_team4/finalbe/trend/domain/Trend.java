package com.final_team4.finalbe.trend.domain;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Trend {
    private Long id;
    private Long categoryId;
    private String keyword;
    private Long searchVolume;
    private LocalDateTime createdAt;
    private String snsType;
}