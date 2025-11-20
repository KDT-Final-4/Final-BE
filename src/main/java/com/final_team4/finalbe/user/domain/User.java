package com.final_team4.finalbe.user.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class User {

    private Long id;

    private Long roleId;

    private String name;

    private String email;

    private String password;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private Integer deleted;

    private String prompt;

    private String apiKey;

    private String llmPublisher;

    private String llmModel;

    private Integer targetTextLength;
}