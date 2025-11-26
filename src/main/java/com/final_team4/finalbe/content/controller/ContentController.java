package com.final_team4.finalbe.content.controller;

import com.final_team4.finalbe._core.security.JwtPrincipal;
import com.final_team4.finalbe.content.dto.*;
import com.final_team4.finalbe.content.service.ContentService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/api/content")
public class ContentController {
    private final ContentService contentService;

    // 검수할 컨텐츠 목록 조회
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<ContentListResponse> getContents(
            @AuthenticationPrincipal JwtPrincipal principal,
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "10") @Positive int size) {
        return contentService.getContents(principal.userId(), page, size);
    }

    // 컨텐츠 상세 조회
    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ContentDetailResponse getContentDetail(
            @AuthenticationPrincipal JwtPrincipal principal,
            @PathVariable Long id) {
        return contentService.getContentDetail(principal.userId(), id);
    }

    // 컨텐츠 등록(파이썬에서 호출)
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ContentCreateResponse createContent(@Valid @RequestBody ContentCreateRequest request) {
        return contentService.createContent(request);
    }

    // 컨텐츠 수정

    // 컨텐츠 상태 변경
}