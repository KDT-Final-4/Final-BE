package com.final_team4.finalbe.trend.controller;

import com.final_team4.finalbe.trend.dto.TrendCreateRequest;
import com.final_team4.finalbe.trend.dto.TrendCreateResponse;
import com.final_team4.finalbe.trend.dto.TrendResponse;
import com.final_team4.finalbe.trend.service.TrendService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/api/trend")
public class TrendController {
    private final TrendService trendService;

    @PostMapping
    public ResponseEntity<TrendCreateResponse> createTrend(@Valid @RequestBody TrendCreateRequest request) {
        TrendCreateResponse response = trendService.createTrend(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<TrendResponse>> getTrends() {
        List<TrendResponse> response = trendService.getTrends();
        return ResponseEntity.ok(response);
    }

}
