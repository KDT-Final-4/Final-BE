package com.final_team4.finalbe.setting.service.llm;

import com.final_team4.finalbe._core.exception.BadRequestException;
import com.final_team4.finalbe._core.exception.ContentNotFoundException;
import com.final_team4.finalbe.setting.domain.llm.LlmChannel;
import com.final_team4.finalbe.setting.dto.llm.LlmChannelCreateRequestDto;
import com.final_team4.finalbe.setting.dto.llm.LlmChannelDetailResponseDto;
import com.final_team4.finalbe.setting.dto.llm.LlmChannelUpdateRequestDto;
import com.final_team4.finalbe.setting.mapper.llm.LlmChannelMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LlmChannelService {

    private final LlmChannelMapper llmChannelMapper;

    /**
     * LLM 설정 조회
     * @param userId 사용자 ID
     * @param id LLM 채널 ID
     * @return LLM 설정 상세 정보
     * @throws ContentNotFoundException LLM 설정을 찾을 수 없는 경우
     */
    public LlmChannelDetailResponseDto findById(Long userId, Long id) {
        LlmChannel entity = llmChannelMapper.findById(userId, id);
        if (entity == null) {
            throw new ContentNotFoundException("LLM 설정을 찾을 수 없습니다.");
        }
        return LlmChannelDetailResponseDto.from(entity);
    }

    /**
     * 사용자의 LLM 설정 조회 (최신 설정)
     * @param userId 사용자 ID
     * @return LLM 설정 상세 정보
     * @throws ContentNotFoundException LLM 설정을 찾을 수 없는 경우
     */
    public LlmChannelDetailResponseDto findByUserId(Long userId) {
        LlmChannel entity = llmChannelMapper.findByUserId(userId);
        if (entity == null) {
            throw new ContentNotFoundException("LLM 설정을 찾을 수 없습니다.");
        }
        return LlmChannelDetailResponseDto.from(entity);
    }

    /**
     * LLM 설정 수정
     * @param userId 사용자 ID
     * @param requestDto 수정 요청 DTO
     * @return 수정된 LLM 설정 상세 정보
     * @throws ContentNotFoundException LLM 설정을 찾을 수 없는 경우
     */
    @Transactional
    public LlmChannelDetailResponseDto update(Long userId, LlmChannelUpdateRequestDto requestDto) {
        LlmChannel entity = llmChannelMapper.findByUserId(userId);

        if (entity == null) {
            throw new ContentNotFoundException("LLM 설정을 찾을 수 없습니다.");
        }

        entity.update(
                requestDto.getName(),
                requestDto.getModelName(),
                requestDto.getApiKey(),
                requestDto.getBaseUrl(),
                requestDto.getStatus(),
                requestDto.getMaxTokens(),
                requestDto.getTemperature(),
                requestDto.getTopP(),
                requestDto.getPrompt()
        );

        llmChannelMapper.update(entity);

        return LlmChannelDetailResponseDto.from(entity);
    }

    /**
     * LLM 설정 등록
     * @param userId 사용자 ID
     * @param requestDto 등록 요청 DTO
     * @return 등록된 LLM 설정 상세 정보
     * @throws BadRequestException 이미 LLM 설정이 존재하거나 필수 필드가 누락된 경우
     */
    @Transactional
    public LlmChannelDetailResponseDto create(Long userId, LlmChannelCreateRequestDto requestDto) {
        // 중복 체크: 사용자당 하나의 설정만 허용
        LlmChannel existing = llmChannelMapper.findByUserId(userId);
        if (existing != null) {
            throw new BadRequestException("이미 LLM 설정이 존재합니다. 수정 API를 사용해주세요.");
        }

        // API 키 필수 검증
        if (!StringUtils.hasText(requestDto.getApiKey())) {
            throw new BadRequestException("API 키는 필수입니다.");
        }

        // baseUrl이 없으면 모델별 기본값 설정
        String baseUrl = requestDto.getBaseUrl();
        if (!StringUtils.hasText(baseUrl)) {
            baseUrl = getDefaultBaseUrl(requestDto.getModelName());
        }

        // 기본값 설정
        Integer maxTokens = requestDto.getMaxTokens() != null 
                ? requestDto.getMaxTokens() 
                : 2000; // 기본값: 2000
        
        BigDecimal temperature = requestDto.getTemperature() != null 
                ? requestDto.getTemperature() 
                : BigDecimal.valueOf(0.7); // 기본값: 0.7
        
        BigDecimal topP = requestDto.getTopP() != null 
                ? requestDto.getTopP() 
                : BigDecimal.valueOf(0.9); // 기본값: 0.9

        Boolean status = requestDto.getStatus() != null 
                ? requestDto.getStatus() 
                : false; // 기본값: false

        LocalDateTime now = LocalDateTime.now();
        LlmChannel entity = LlmChannel.builder()
                .userId(userId)
                .name(requestDto.getName())
                .modelName(requestDto.getModelName())
                .apiKey(requestDto.getApiKey())
                .baseUrl(baseUrl)
                .status(status)
                .maxTokens(maxTokens)
                .temperature(temperature)
                .topP(topP)
                .prompt(requestDto.getPrompt())
                .createdAt(now)
                .updatedAt(now)
                .build();

        llmChannelMapper.insert(entity);
        return LlmChannelDetailResponseDto.from(entity);
    }

    /**
     * 모델명에 따른 기본 baseUrl 반환
     * @param modelName 모델명
     * @return 기본 baseUrl
     */
    private String getDefaultBaseUrl(String modelName) {
        if (modelName == null) {
            return "https://api.openai.com/v1"; // 기본값
        }

        String lowerModelName = modelName.toLowerCase();

        // OpenAI 모델 (gpt, gpt-3.5, gpt-4 등)
        if (lowerModelName.startsWith("gpt") || lowerModelName.contains("openai")) {
            return "https://api.openai.com/v1";
        }

        // Anthropic 모델 (claude 등)
        if (lowerModelName.contains("claude") || lowerModelName.contains("anthropic")) {
            return "https://api.anthropic.com/v1";
        }

        // Google 모델 (gemini, palm 등)
        if (lowerModelName.contains("gemini") || lowerModelName.contains("palm") || lowerModelName.contains("google")) {
            return "https://generativelanguage.googleapis.com/v1";
        }

        // 기본값: OpenAI
        return "https://api.openai.com/v1";
    }
}

