package com.final_team4.finalbe.content.domain;

import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Content {

    @NotNull
    private Long id;

    @NotNull
    private String jobId;

    @NotNull
    private Long userId;

    @NotNull
    private Long uploadChannelId;

    @NotNull
    private String title;

    @NotNull
    private String body;

    @NotNull
    private ContentStatus status; // 검수전/승인/반려

    @NotNull
    private ContentGenType generationType; // 자동생성/수동생성

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    public void updateContent(String title, String body) {
        this.title = title;
        this.body = body;
        this.updatedAt = LocalDateTime.now();
    }

    public void updateStatus(ContentStatus status) {
        this.status = status;
        this.updatedAt = LocalDateTime.now();
    }

}
