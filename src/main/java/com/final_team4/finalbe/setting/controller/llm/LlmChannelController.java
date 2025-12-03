package com.final_team4.finalbe.setting.controller.llm;

import com.final_team4.finalbe._core.security.JwtPrincipal;
import com.final_team4.finalbe.setting.dto.llm.LlmChannelCreateRequestDto;
import com.final_team4.finalbe.setting.dto.llm.LlmChannelDetailResponseDto;
import com.final_team4.finalbe.setting.dto.llm.LlmChannelUpdateRequestDto;
import com.final_team4.finalbe.setting.service.llm.LlmChannelService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/setting/llm")
public class LlmChannelController {

    private final LlmChannelService llmChannelService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public LlmChannelDetailResponseDto getLlmSetting(@AuthenticationPrincipal JwtPrincipal principal) {
        if (principal == null) {
            throw new IllegalStateException("인증된 사용자 정보를 찾을 수 없습니다.");
        }
        return llmChannelService.findByUserId(principal.userId());
    }

    /**
     * LLM 설정 등록
     * 
     * @param principal 인증된 사용자 정보
     * @param requestDto 등록 요청 DTO
     * @return 등록된 LLM 설정 상세 정보
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public LlmChannelDetailResponseDto createLlmSetting(
            @AuthenticationPrincipal JwtPrincipal principal,
            @Valid @RequestBody LlmChannelCreateRequestDto requestDto) {
        if (principal == null) {
            throw new IllegalStateException("인증된 사용자 정보를 찾을 수 없습니다.");
        }
        return llmChannelService.create(principal.userId(), requestDto);
    }

    @PutMapping
    @ResponseStatus(HttpStatus.OK)
    public LlmChannelDetailResponseDto updateLlmSetting(
            @AuthenticationPrincipal JwtPrincipal principal,
            @Valid @RequestBody LlmChannelUpdateRequestDto requestDto) {
        if (principal == null) {
            throw new IllegalStateException("인증된 사용자 정보를 찾을 수 없습니다.");
        }
        return llmChannelService.update(principal.userId(), requestDto);
    }
}

