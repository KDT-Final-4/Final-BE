package com.final_team4.finalbe.content.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ContentProductRequestDto {

    @NotBlank
    private String title;

    @NotBlank
    private String link;

    private String thumbnail;

    @NotNull
    private Long price;

    @NotBlank
    private String category;
}
