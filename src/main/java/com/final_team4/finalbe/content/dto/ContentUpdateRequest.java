package com.final_team4.finalbe.content.dto;

import com.final_team4.finalbe.content.domain.ContentGenType;
import com.final_team4.finalbe.content.domain.ContentStatus;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ContentUpdateRequest {

    @NotNull
    private Long uploadChannelId;

    @NotNull
    private String title;

    @NotNull
    private String body;

    @NotNull
    private ContentStatus status;

    @NotNull
    private ContentGenType generationType;

}
