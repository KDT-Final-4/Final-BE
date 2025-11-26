package com.final_team4.finalbe.trend.controller;

import com.final_team4.finalbe._core.security.JwtPrincipal;
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
    public TrendCreateResponse createTrend(@Valid @RequestBody TrendCreateRequest request) {
        return trendService.createTrend(request);
    }

    // 인기검색어 목록 조회
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<TrendResponse> getTrends(
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "10") @Positive int size) {
        return trendService.getTrends(page, size);
    }

    // 인기검색어 컨텐츠 생성 요청(python에 요청)
    @PostMapping("/content")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public TrendCreateContentResponse requestCreateContent(
            @RequestBody @Valid TrendCreateContentRequest request,
            @AuthenticationPrincipal JwtPrincipal principal) {
        return trendService.requestCreateContent(request, principal.userId());
    }

}
