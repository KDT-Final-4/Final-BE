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
                requestDto.getStatus(),
                requestDto.getMaxTokens(),
                requestDto.getTemperature(),
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

        Boolean requestedStatus = requestDto.getStatus();
        boolean active = Boolean.TRUE.equals(requestedStatus); // null/false일때는 비활성

        // 활성일 때만 API 키 필수
        if (active && !StringUtils.hasText(requestDto.getApiKey())) {
            throw new BadRequestException("API 키는 필수입니다.");
        }

        // 중복 체크: 사용자당 하나의 설정만 허용
        LlmChannel existing = llmChannelMapper.findByUserId(userId);
        if (existing != null) {
            throw new BadRequestException("이미 LLM 설정이 존재합니다. 수정 API를 사용해주세요.");
        }

        // 기본값 설정
        Integer maxTokens = requestDto.getMaxTokens() != null 
                ? requestDto.getMaxTokens() 
                : 2000; // 기본값: 2000
        
        BigDecimal temperature = requestDto.getTemperature() != null 
                ? requestDto.getTemperature() 
                : BigDecimal.valueOf(0.7); // 기본값: 0.7

        Boolean status = requestDto.getStatus() != null 
                ? requestDto.getStatus() 
                : false; // 기본값: false

        LocalDateTime now = LocalDateTime.now();
        LlmChannel entity = LlmChannel.builder()
                .userId(userId)
                .name(requestDto.getName())
                .modelName(requestDto.getModelName())
                .apiKey(requestDto.getApiKey())
                .status(status)
                .maxTokens(maxTokens)
                .temperature(temperature)
                .prompt(requestDto.getPrompt())
                .createdAt(now)
                .updatedAt(now)
                .build();

        llmChannelMapper.insert(entity);
        return LlmChannelDetailResponseDto.from(entity);
    }
}

