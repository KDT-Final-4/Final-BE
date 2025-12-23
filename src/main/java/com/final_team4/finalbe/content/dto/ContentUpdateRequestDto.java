package com.final_team4.finalbe.content.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ContentUpdateRequestDto {

    @NotNull
    private String title;

    @NotNull
    private String body;

}
