package com.final_team4.finalbe.trend.service;

import com.final_team4.finalbe.trend.dto.TrendCreateRequest;
import com.final_team4.finalbe.trend.dto.TrendCreateResponse;
import com.final_team4.finalbe.trend.dto.TrendResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SpringBootTest
@Transactional
class TrendServiceTest {

    @Autowired
    private TrendService trendService;

    private TrendCreateRequest createRequest(Long categoryId,
                                             String keyword,
                                             Long searchVolume,
                                             LocalDateTime createdAt,
                                             String snsType) {
        TrendCreateRequest request = mock(TrendCreateRequest.class);
        when(request.getCategoryId()).thenReturn(categoryId);
        when(request.getKeyword()).thenReturn(keyword);
        when(request.getSearchVolume()).thenReturn(searchVolume);
        when(request.getCreatedAt()).thenReturn(createdAt);
        when(request.getSnsType()).thenReturn(snsType);
        return request;
    }

    @DisplayName("트렌드 생성 성공")
    @Test
    void createTrend() {
        // given
        LocalDateTime createdAt = LocalDateTime.now().minusDays(1);
        TrendCreateRequest request = createRequest(1L, "keyword", 1000L, createdAt, "INSTAGRAM");

        // when
        TrendCreateResponse response = trendService.createTrend(request);

        // then
        assertThat(response.getId()).isNotNull();
        assertThat(response.getCategoryId()).isEqualTo(1L);
        assertThat(response.getKeyword()).isEqualTo("keyword");
        assertThat(response.getSearchVolume()).isEqualTo(1000L);
        assertThat(response.getCreatedAt()).isEqualTo(createdAt);
        assertThat(response.getSnsType()).isEqualTo("INSTAGRAM");
    }

    @DisplayName("트렌드 전체 조회 성공")
    @Test
    void getTrends() {
        // given
        TrendCreateRequest first = createRequest(1L, "keyword-1", 500L, LocalDateTime.now().minusHours(2), "YOUTUBE");
        TrendCreateRequest second = createRequest(1L, "keyword-2", 700L, LocalDateTime.now().minusHours(1), "INSTAGRAM");
        trendService.createTrend(first);
        trendService.createTrend(second);

        // when
        List<TrendResponse> responses = trendService.getTrends();

        // then
        assertThat(responses).isNotEmpty();
        assertThat(responses).extracting(TrendResponse::getKeyword)
                .contains("keyword-1", "keyword-2");

        TrendResponse keyword2 = responses.stream()
                .filter(response -> response.getKeyword().equals("keyword-2"))
                .findFirst()
                .orElseThrow();

        assertThat(keyword2.getCategoryId()).isEqualTo(1L);
        assertThat(keyword2.getSearchVolume()).isEqualTo(700L);
        assertThat(keyword2.getSnsType()).isEqualTo("INSTAGRAM");
    }
}
