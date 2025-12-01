package com.final_team4.finalbe.setting.service.llm;

import com.final_team4.finalbe._core.exception.ContentNotFoundException;
import com.final_team4.finalbe.setting.domain.llm.LlmChannel;
import com.final_team4.finalbe.setting.dto.llm.LlmChannelDetailResponseDto;
import com.final_team4.finalbe.setting.dto.llm.LlmChannelUpdateRequestDto;
import com.final_team4.finalbe.setting.mapper.llm.LlmChannelMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
                requestDto.getTopP()
        );

        llmChannelMapper.update(entity);

        return LlmChannelDetailResponseDto.from(entity);
    }
}

