package com.final_team4.finalbe.setting.domain.llm;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class LlmChannel {
    private Long id;
    private Long userId;
    private String name;
    private String modelName;
    private String apiKey;
    private Boolean status; // true: 활성, false: 비활성
    private Integer maxTokens;
    private BigDecimal temperature;
    private String prompt; // CLOB 타입
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public void update(String name, String modelName, String apiKey, 
                      Boolean status, Integer maxTokens, BigDecimal temperature, String prompt) {
        if (name != null) this.name = name;
        if (modelName != null) this.modelName = modelName;
        if (apiKey != null) this.apiKey = apiKey;
        if (status != null) this.status = status;
        if (maxTokens != null) this.maxTokens = maxTokens;
        if (temperature != null) this.temperature = temperature;
        if (prompt != null) this.prompt = prompt;
        this.updatedAt = LocalDateTime.now();
    }

    public void updateStatus(Boolean status) {
        if (status != null) {
            this.status = status;
            this.updatedAt = LocalDateTime.now();
        }
    }
}

