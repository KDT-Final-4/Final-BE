package com.final_team4.finalbe.trend.service;

import com.final_team4.finalbe.trend.dto.*;
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

    private TrendCreateRequestDto createRequestDto(Long categoryId,
                                             String keyword,
                                             Long searchVolume,
                                             String snsType) {
        return TrendCreateRequestDto.builder()
                .categoryId(categoryId)
                .keyword(keyword)
                .searchVolume(searchVolume)
                .snsType(snsType)
                .build();
    }

    @DisplayName("인기검색어 저장_성공")
    @Test
    void createTrend() {
        // given
        TrendCreateRequestDto requestDto = createRequestDto(1L, "test", 500L, "X");


        // when
        TrendCreateResponseDto response = trendService.createTrend(requestDto);

        // then
        assertThat(response.getId()).isNotNull();
        assertThat(response.getCategoryId()).isEqualTo(1L);
        assertThat(response.getKeyword()).isEqualTo("test");
        assertThat(response.getSearchVolume()).isEqualTo(500L);
        assertThat(response.getSnsType()).isEqualTo("X");
    }

    @DisplayName("인기검색어 목록 조회_성공")
    @Test
    void getTrends() {
        // given
        TrendCreateRequestDto first = createRequestDto(1L, "keyword-1", 500L, "YOUTUBE");
        TrendCreateRequestDto second = createRequestDto(2L, "keyword-2", 700L, "INSTAGRAM");
        TrendCreateRequestDto third = createRequestDto(1L, "keyword-3", 900L, "TIKTOK");
        trendService.createTrend(first);
        trendService.createTrend(second);
        trendService.createTrend(third);

        // when
        List<TrendResponseDto> firstPage = trendService.getTrends(0, 2);
        List<TrendResponseDto> secondPage = trendService.getTrends(1, 2);

        // then
        assertThat(firstPage)
                .hasSize(2)
                .extracting(TrendResponseDto::getKeyword)
                .containsExactly(third.getKeyword(), second.getKeyword());

        assertThat(secondPage)
                .hasSize(1)
                .first()
                .satisfies(trend -> {
                    assertThat(trend.getKeyword()).isEqualTo(first.getKeyword());
                    assertThat(trend.getCategoryId()).isEqualTo(first.getCategoryId());
                });
    }
}
