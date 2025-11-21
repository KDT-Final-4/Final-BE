package com.final_team4.finalbe.trend.dto;

import jakarta.validation.constraints.*;
import lombok.*;

@Builder
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TrendCreateRequest {

    @NotNull
    private Long categoryId;

    private String keyword;

    private Long searchVolume;

    private String snsType;
}
