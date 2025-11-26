package com.final_team4.finalbe.content.dto;

import com.final_team4.finalbe.content.domain.ContentStatus;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ContentStatusUpdateRequest {

    @NotNull
    private ContentStatus status;

}
