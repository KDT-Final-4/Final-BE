package com.final_team4.finalbe.setting.dto.llm;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
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

    private Boolean status; // true: 활성, false: 비활성

    @Min(1)
    private Integer maxTokens;

    @DecimalMin("0.0")
    @DecimalMax("2.0")
    private BigDecimal temperature;

    @DecimalMin("0.0")
    @DecimalMax("1.0")
    private BigDecimal topP;
    
    // 주의: userId는 보안상 Request Body에 포함하지 않고, 
    // 컨트롤러에서 @AuthenticationPrincipal로 주입받아 사용
}

