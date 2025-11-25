package com.final_team4.finalbe.uploadChannel.domain;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UploadChannel {
    private Long id;
    private Long userId;
    private Channel name;
    private String apiKey;
    private Boolean status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}