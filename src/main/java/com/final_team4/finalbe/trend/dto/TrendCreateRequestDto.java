package com.final_team4.finalbe.trend.dto;

import com.final_team4.finalbe.trend.domain.TrendSnsType;
import jakarta.validation.constraints.*;
import lombok.*;

@Builder
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TrendCreateRequestDto {

    @NotNull
    private Long categoryId;

    private String keyword;

    private Long searchVolume;

    private TrendSnsType snsType;
}
