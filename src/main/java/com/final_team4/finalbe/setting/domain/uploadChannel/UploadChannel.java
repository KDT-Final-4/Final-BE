package com.final_team4.finalbe.setting.domain.uploadChannel;

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
    private String clientId;
    private String clientPw;
    private String blogId;
    private Boolean status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public void update(String apiKey, String clientId, String clientPw, String blogId, Boolean status) {
        if(apiKey != null) this.apiKey = apiKey;
        if(clientId != null) this.clientId = clientId;
        if(clientPw != null) this.clientPw = clientPw;
        if(blogId != null) this.blogId = blogId;
        this.status = status;
        this.updatedAt = LocalDateTime.now();
    }
}