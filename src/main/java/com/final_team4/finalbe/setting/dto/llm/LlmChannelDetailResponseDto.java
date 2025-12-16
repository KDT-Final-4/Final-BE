package com.final_team4.finalbe.setting.dto.llm;

import com.final_team4.finalbe.content.domain.ContentGenType;
import com.final_team4.finalbe.setting.domain.llm.LlmChannel;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonFormat;

@Getter
@Builder
public class LlmChannelDetailResponseDto {
    private Long id;
    private Long userId;
    private String name;
    private String modelName;
    private Boolean status;
    private Integer maxTokens;
    private BigDecimal temperature;
    private String prompt; // CLOB 타입
    private String apiKey; // 마스킹 처리된 값
    private ContentGenType generationType; // 자동생성/수동생성
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'", timezone = "Asia/Seoul")
    private LocalDateTime createdAt;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'", timezone = "Asia/Seoul")
    private LocalDateTime updatedAt;

    public static LlmChannelDetailResponseDto from(LlmChannel entity) {
        return LlmChannelDetailResponseDto.builder()
                .id(entity.getId())
                .userId(entity.getUserId())
                .name(entity.getName())
                .modelName(entity.getModelName())
                .status(entity.getStatus())
                .maxTokens(entity.getMaxTokens())
                .temperature(entity.getTemperature())
                .prompt(entity.getPrompt())
                .apiKey(maskApiKey(entity.getApiKey()))
                .generationType(entity.getGenerationType())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    private static String maskApiKey(String apiKey) {
        if (apiKey == null || apiKey.length() <= 4) {
            return apiKey;
        }
        // 마지막 4자리만 보여주고 나머지는 마스킹
        int length = apiKey.length();
        String lastFour = apiKey.substring(length - 4);
        return "*".repeat(Math.max(0, length - 4)) + lastFour;
    }
}

