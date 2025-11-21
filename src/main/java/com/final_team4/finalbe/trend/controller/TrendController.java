package com.final_team4.finalbe.trend.controller;

import com.final_team4.finalbe.trend.dto.TrendCreateRequest;
import com.final_team4.finalbe.trend.dto.TrendCreateResponse;
import com.final_team4.finalbe.trend.dto.TrendResponse;
import com.final_team4.finalbe.trend.service.TrendService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/api/trend")
public class TrendController {
    private final TrendService trendService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TrendCreateResponse createTrend(@Valid @RequestBody TrendCreateRequest request) {
        return trendService.createTrend(request);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<TrendResponse> getTrends(
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "10") @Positive int size) {
        return trendService.getTrends(page, size);
    }

}
