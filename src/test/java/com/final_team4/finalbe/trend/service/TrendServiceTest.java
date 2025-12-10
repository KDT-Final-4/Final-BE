package com.final_team4.finalbe.trend.service;

import com.final_team4.finalbe.trend.domain.TrendSnsType;
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
                                             TrendSnsType snsType) {
        return TrendCreateRequestDto.builder()
                .categoryId(categoryId)
                .keyword(keyword)
                .searchVolume(searchVolume)
                .snsType(snsType)
                .build();
    }

    @DisplayName("인기검색어 저장_성공")
    @Test
    void createTrends() {
        // given
        TrendCreateRequestDto requestDto = createRequestDto(1L, "test", 500L, TrendSnsType.X);

        // when
        List<TrendCreateResponseDto> responses = trendService.createTrends(List.of(requestDto));

        // then
        TrendCreateResponseDto response = responses.getFirst();
        assertThat(response.getId()).isNotNull();
        assertThat(response.getCategoryId()).isEqualTo(1L);
        assertThat(response.getKeyword()).isEqualTo("test");
        assertThat(response.getSearchVolume()).isEqualTo(500L);
        assertThat(response.getSnsType()).isEqualTo(TrendSnsType.X);
    }

    @DisplayName("인기검색어 목록 조회_성공")
    @Test
    void getTrends() {
        // given
        long initialTotalCount = trendService.getTrends(0, 1, null).getTotalCount();
        long initialInstagramCount = trendService.getTrends(0, 1, TrendSnsType.INSTAGRAM).getTotalCount();
        TrendCreateRequestDto first = createRequestDto(1L, "keyword-1", 500L, TrendSnsType.GOOGLE);
        TrendCreateRequestDto second = createRequestDto(2L, "keyword-2", 700L, TrendSnsType.INSTAGRAM);
        TrendCreateRequestDto third = createRequestDto(1L, "keyword-3", 900L, TrendSnsType.X);
        trendService.createTrends(List.of(first, second, third));

        // when
        TrendListResponseDto firstPage = trendService.getTrends(0, 2, null);
        TrendListResponseDto secondPage = trendService.getTrends(1, 2, null);
        int aggregatedSize = (int) Math.min(Integer.MAX_VALUE, initialTotalCount + 3);
        TrendListResponseDto aggregated = trendService.getTrends(0, aggregatedSize, null);

        // then
        assertThat(firstPage.getItems()).hasSizeLessThanOrEqualTo(2);
        assertThat(secondPage.getItems()).hasSizeLessThanOrEqualTo(2);

        assertThat(aggregated.getItems())
                .extracting(TrendResponseDto::getKeyword)
                .contains(first.getKeyword(), second.getKeyword(), third.getKeyword());

        assertThat(aggregated.getItems())
                .filteredOn(trend -> trend.getKeyword().equals(first.getKeyword()))
                .first()
                .satisfies(trend -> assertThat(trend.getCategoryName()).isNotBlank());

        assertThat(aggregated.getItems())
                .filteredOn(trend -> trend.getKeyword().equals(second.getKeyword()))
                .first()
                .satisfies(trend -> assertThat(trend.getCategoryName()).isNotBlank());

        assertThat(firstPage.getTotalCount()).isEqualTo(initialTotalCount + 3);
        assertThat(secondPage.getTotalCount()).isEqualTo(initialTotalCount + 3);

        // snsType filtering
        int instagramSize = (int) Math.min(Integer.MAX_VALUE, initialInstagramCount + 1);
        TrendListResponseDto instagramOnly = trendService.getTrends(0, instagramSize, TrendSnsType.INSTAGRAM);
        assertThat(instagramOnly.getItems())
                .extracting(TrendResponseDto::getKeyword)
                .contains(second.getKeyword());
        assertThat(instagramOnly.getItems()).allSatisfy(trend -> assertThat(trend.getCategoryName()).isNotBlank());
        assertThat(instagramOnly.getTotalCount()).isEqualTo(initialInstagramCount + 1);
    }
}
