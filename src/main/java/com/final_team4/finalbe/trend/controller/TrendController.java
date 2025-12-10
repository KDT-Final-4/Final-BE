package com.final_team4.finalbe.trend.controller;

import com.final_team4.finalbe._core.security.JwtPrincipal;
import com.final_team4.finalbe.trend.domain.TrendSnsType;
import com.final_team4.finalbe.trend.dto.*;
import com.final_team4.finalbe.trend.service.TrendService;
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
@RequestMapping("/api/trend")
public class TrendController {
    private final TrendService trendService;

    // 인기검색어 삽입 (python 호출용)
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public List<TrendCreateResponseDto> createTrends(@RequestBody @Valid List<TrendCreateRequestDto> requests) {
        return trendService.createTrends(requests);
    }

    // 인기검색어 목록 조회
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public TrendListResponseDto getTrends(
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "10") @Positive int size,
            @RequestParam(required = false) TrendSnsType snsType) {
        return trendService.getTrends(page, size, snsType);
    }

    // 인기검색어 컨텐츠 생성 요청(python에 요청)
    @PostMapping("/content")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public TrendCreateContentResponseDto requestCreateContent(
            @RequestBody @Valid TrendCreateContentRequestDto request,
            @AuthenticationPrincipal JwtPrincipal principal) {
        return trendService.requestCreateContent(request, principal.userId());
    }

}
