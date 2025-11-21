package com.final_team4.finalbe.trend.service;

import com.final_team4.finalbe.trend.dto.TrendCreateRequest;
import com.final_team4.finalbe.trend.dto.TrendCreateResponse;
import com.final_team4.finalbe.trend.dto.TrendResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class TrendServiceTest {

    @Autowired
    private TrendService trendService;

    private TrendCreateRequest createRequest(Long categoryId,
                                             String keyword,
                                             Long searchVolume,
                                             String snsType) {
        return TrendCreateRequest.builder()
                .categoryId(categoryId)
                .keyword(keyword)
                .searchVolume(searchVolume)
                .snsType(snsType)
                .build();
    }

    @DisplayName("트렌드 생성 성공")
    @Test
    void createTrend() {
        // given
        TrendCreateRequest request = createRequest(1L, "keyword", 1000L, "INSTAGRAM");

        // when
        TrendCreateResponse response = trendService.createTrend(request);

        // then
        assertThat(response.getId()).isNotNull();
        assertThat(response.getCategoryId()).isEqualTo(1L);
        assertThat(response.getKeyword()).isEqualTo("keyword");
        assertThat(response.getSearchVolume()).isEqualTo(1000L);
        assertThat(response.getSnsType()).isEqualTo("INSTAGRAM");
    }

    @DisplayName("트렌드 페이징 조회 성공")
    @Test
    void getTrendsWithPagination() throws InterruptedException {
        // given
        TrendCreateRequest first = createRequest(1L, "keyword-1", 500L, "YOUTUBE");
        TrendCreateRequest second = createRequest(1L, "keyword-2", 700L, "INSTAGRAM");
        TrendCreateRequest third = createRequest(1L, "keyword-3", 900L, "TIKTOK");
        trendService.createTrend(first);
        Thread.sleep(5);
        trendService.createTrend(second);
        Thread.sleep(5);
        trendService.createTrend(third);

        // when
        List<TrendResponse> firstPage = trendService.getTrends(0, 2);
        List<TrendResponse> secondPage = trendService.getTrends(1, 2);

        // then
        assertThat(firstPage).hasSize(2);
        assertThat(firstPage).extracting(TrendResponse::getKeyword)
                .containsExactly("keyword-3", "keyword-2");
        assertThat(secondPage).hasSize(1);
        assertThat(secondPage.getFirst().getKeyword()).isEqualTo("keyword-1");
    }
}
