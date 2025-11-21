package com.final_team4.finalbe.trend.service;

import com.final_team4.finalbe.trend.domain.Trend;
import com.final_team4.finalbe.trend.dto.TrendCreateRequest;
import com.final_team4.finalbe.trend.dto.TrendCreateResponse;
import com.final_team4.finalbe.trend.dto.TrendResponse;
import com.final_team4.finalbe.trend.mapper.TrendMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TrendService {

    private final TrendMapper trendMapper;

    @Transactional
    public TrendCreateResponse createTrend(TrendCreateRequest request) {
        Trend trend = Trend.builder()
                .categoryId(request.getCategoryId())
                .keyword(request.getKeyword())
                .searchVolume(request.getSearchVolume())
                .createdAt(LocalDateTime.now())
                .snsType(request.getSnsType())
                .build();
        trendMapper.insert(trend);

        return TrendCreateResponse.from(trend);
    }

    public List<TrendResponse> getTrends() {
        List<Trend> trends = trendMapper.findAll();
        return trends.stream()
                .map(TrendResponse::from)
                .toList();
    }

}
