package com.final_team4.finalbe.trend.service;

import com.final_team4.finalbe.trend.domain.Trend;
import com.final_team4.finalbe.trend.dto.TrendCreateRequest;
import com.final_team4.finalbe.trend.dto.TrendCreateResponse;
import com.final_team4.finalbe.trend.mapper.TrendMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class TrendService {

    private final TrendMapper trendMapper;

    public TrendCreateResponse createTrend(TrendCreateRequest request) {
        Trend trend = Trend.builder()
                .categoryId(request.getCategoryId())
                .keyword(request.getKeyword())
                .searchVolume(request.getSearchVolume())
                .createdAt(request.getCreatedAt() != null ? request.getCreatedAt() : LocalDateTime.now())
                .snsType(request.getSnsType())
                .build();
        trendMapper.insert(trend);

        return TrendCreateResponse.from(trend);
  }

}
