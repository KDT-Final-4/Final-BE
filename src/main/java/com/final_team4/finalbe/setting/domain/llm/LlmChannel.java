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
    private String baseUrl;
    private Boolean status; // true: 활성, false: 비활성
    private Integer maxTokens;
    private BigDecimal temperature;
    private BigDecimal topP;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public void update(String name, String modelName, String apiKey, String baseUrl, 
                      Boolean status, Integer maxTokens, BigDecimal temperature, BigDecimal topP) {
        if (name != null) this.name = name;
        if (modelName != null) this.modelName = modelName;
        if (apiKey != null) this.apiKey = apiKey;
        if (baseUrl != null) this.baseUrl = baseUrl;
        if (status != null) this.status = status;
        if (maxTokens != null) this.maxTokens = maxTokens;
        if (temperature != null) this.temperature = temperature;
        if (topP != null) this.topP = topP;
        this.updatedAt = LocalDateTime.now();
    }

    public void updateStatus(Boolean status) {
        if (status != null) {
            this.status = status;
            this.updatedAt = LocalDateTime.now();
        }
    }
}

