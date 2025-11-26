package com.final_team4.finalbe.trend.dto;

import jakarta.validation.constraints.*;
import lombok.AccessLevel;
import lombok.*;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class TrendCreateContentRequest {

    @NotBlank
    private String keyword;
}
