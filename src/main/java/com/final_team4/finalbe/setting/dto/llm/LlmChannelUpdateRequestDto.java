package com.final_team4.finalbe.setting.dto.llm;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@Builder
public class LlmChannelUpdateRequestDto {
    @Size(max = 50)
    private String name;

    @NotNull
    @Size(max = 100)
    private String modelName;

    @Size(max = 200)
    private String apiKey;

    @Size(max = 200)
    private String baseUrl;

    private Integer status; // 0: 비활성, 1: 활성

    private Integer maxTokens;

    private BigDecimal temperature;

    private BigDecimal topP;
    
    // 주의: userId는 보안상 Request Body에 포함하지 않고, 
    // 컨트롤러에서 @AuthenticationPrincipal로 주입받아 사용
}

