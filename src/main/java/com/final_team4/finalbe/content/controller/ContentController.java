package com.final_team4.finalbe.content.controller;

import com.final_team4.finalbe.content.dto.*;
import com.final_team4.finalbe.content.service.ContentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/api/content")
public class ContentController {
    private final ContentService contentService;

    // 검수할 컨텐츠 목록 조회

    // 컨텐츠 상세 조회

    // 컨텐츠 등록(파이썬에서 호출)
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ContentCreateResponse createContent(@Valid @RequestBody ContentCreateRequest request) {
        return contentService.createContent(request);
    }

    // 컨텐츠 수정

    // 컨텐츠 상태 변경
}