package com.final_team4.finalbe.trend.domain;

import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Trend {

    @NotNull
    private Long id;

    @NotNull
    private Long categoryId;

    private String keyword;

    private Long searchVolume;

    private LocalDateTime createdAt;

    private String snsType;
}