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
    private final RestClientCallerService<TrendCreateContentPayloadDto> restClientCallerService;

    // 인기검색어 저장(python 호출용)
    @Transactional
    public TrendCreateResponseDto createTrend(TrendCreateRequestDto request) {
        Trend trend = Trend.builder()
                .categoryId(request.getCategoryId())
                .keyword(request.getKeyword())
                .searchVolume(request.getSearchVolume())
                .createdAt(LocalDateTime.now())
                .snsType(request.getSnsType())
                .build();
        trendMapper.insert(trend);

        return TrendCreateResponseDto.from(trend);
    }

    // 인기검색어 목록 조회
    public List<TrendResponseDto> getTrends(int page, int size) {
        int offset = page * size;
        List<Trend> trends = trendMapper.findAll(size, offset);
        return trends.stream()
                .map(TrendResponseDto::from)
                .toList();
    }

    // 인기검색어 컨텐츠 생성 요청(python에 요청)
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public TrendCreateContentResponseDto requestCreateContent(TrendCreateContentRequestDto request, Long userId) {
        // 유저 업로드 채널 정보 조회
        List<UploadChannelItemPayload> channels = uploadChannelService.getChannelsByUserId(userId);
        if (channels == null || channels.isEmpty()) {
            throw new ContentNotFoundException("등록된 업로드 채널이 없습니다.");
        }

        // UUID 생성
        UUID jobId = UuidCreator.getTimeOrderedEpoch();

        // 파이썬 서비스 호출
        TrendCreateContentPayloadDto payload = TrendCreateContentPayloadDto.of(userId, request.getKeyword(), channels, jobId);
        boolean requested = restClientCallerService.callGeneratePosts(payload);

        return TrendCreateContentResponseDto.of(request.getKeyword(), requested);
    }

}
