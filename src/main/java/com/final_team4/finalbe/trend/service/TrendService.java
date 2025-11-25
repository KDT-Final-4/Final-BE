package com.final_team4.finalbe.trend.service;

import com.final_team4.finalbe._core.exception.ContentNotFoundException;
import com.final_team4.finalbe.restClient.service.RestClientCallerService;
import com.final_team4.finalbe.trend.domain.Trend;
import com.final_team4.finalbe.trend.dto.*;
import com.final_team4.finalbe.trend.mapper.TrendMapper;
import com.final_team4.finalbe.uploadChannel.dto.UploadChannelItemPayload;
import com.final_team4.finalbe.uploadChannel.service.UploadChannelService;
import com.github.f4b6a3.uuid.UuidCreator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Transactional(readOnly = true)
@Service
@RequiredArgsConstructor
public class TrendService {

    private final TrendMapper trendMapper;
    private final UploadChannelService uploadChannelService;
    private final RestClientCallerService<TrendCreateContentPayload> restClientCallerService;

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

    public List<TrendResponse> getTrends(int page, int size) {
        int offset = page * size;
        List<Trend> trends = trendMapper.findAll(size, offset);
        return trends.stream()
                .map(TrendResponse::from)
                .toList();
    }

    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public TrendCreateContentResponse requestCreateContent(TrendCreateContentRequest request) {
        // 유저 업로드 채널 정보 조회
        List<UploadChannelItemPayload> channels = uploadChannelService.getChannelsByUserId(request.getUserId());
        if (channels == null || channels.isEmpty()) {
            throw new ContentNotFoundException("등록된 업로드 채널이 없습니다.");
        }

        // UUID 생성
        UUID jopId = UuidCreator.getTimeOrderedEpoch();

        TrendCreateContentPayload payload = TrendCreateContentPayload.of(request.getUserId(), request.getKeyword(), channels, jopId);
        boolean requested = restClientCallerService.callGeneratePosts(payload);

        return TrendCreateContentResponse.of(request.getKeyword(), requested);
    }

}
