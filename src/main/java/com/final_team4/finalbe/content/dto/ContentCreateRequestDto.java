package com.final_team4.finalbe.content.dto;

import com.final_team4.finalbe.content.domain.ContentGenType;
import com.final_team4.finalbe.content.domain.ContentStatus;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Builder
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ContentCreateRequestDto {

    @NotNull
    private String jobId;

    @NotNull
    private Long uploadChannelId;

    @NotNull
    private Long userId;

    @NotNull
    private String title;

    @NotNull
    private String body;

    @NotNull
    private ContentStatus status;

    @NotNull
    private ContentGenType generationType;

    private String contentLink;

    private String keyword;

    @Valid
    @NotNull
    private ContentProductRequestDto product;

}
