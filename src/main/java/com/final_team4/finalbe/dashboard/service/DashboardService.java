package com.final_team4.finalbe.dashboard.service;


import com.final_team4.finalbe.dashboard.dto.*;
import com.final_team4.finalbe.dashboard.mapper.ClicksMapper;
import com.final_team4.finalbe.dashboard.mapper.DashboardMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;


@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DashboardService {

    private final ClicksMapper clicksMapper;
    private final DashboardMapper dashboardMapper;


    public DashboardStatusGetResponseDto getStatus(Long  userId) {
        long clicks = clicksMapper.countAllClicksByUserId(userId);

        return DashboardStatusGetResponseDto.builder()
                .allClicks(clicks)
                .allViews(0) // 아직  구현할지 미정이고 추후 구현 가능성 때문에 우선 0으로 넣어둠
                .visitors(0)
                .averageDwellTime(0)
                .build();
    }

    public DashboardContentsResponseDto getContents(Long userId) {

        List<DashboardContentSummary> summaries = dashboardMapper.findContentsByUserId(userId);

        List<DashboardContentItemDto> items = new ArrayList<>();
        for (DashboardContentSummary summary : summaries) {
            items.add(DashboardContentItemDto.from(summary));

        }

        return DashboardContentsResponseDto.from(items);
    }

    public DashboardDailyClicksResponseDto getDailyClicks(Long userId, LocalDate start, LocalDate end) {
        // end가 start보다 빠르면 잘못된 입력
        if (end.isBefore(start)) {
            throw new IllegalArgumentException("end 날짜는 start 날짜보다 빠를 수 없습니다.");
        }

        // DB에서 가져온 일별 클릭 합계를 날짜별로 바로 찾을 수 있게 Map으로 변환
        Map<LocalDate, Long> clicksByDate = clicksMapper.findDailyClicks(userId, start, end).stream()
                .collect(Collectors.toMap(
                        dto -> LocalDate.parse(dto.getDate()), //DailyClicksDto의 날짜 문자열을 LocalDate 키로 변환
                        DailyClicksDto::getClicks,
                        Long::sum)); //날짜가 중복되어 들어오면 합산해서 하나로 합침

        List<DailyClicksDto> dailyClicks = new ArrayList<>();
        // start부터 end까지 하루씩 증가시키며 빈 날짜는 0으로 채움
        //!day.isAfter(end) ==   day <= end
        for (LocalDate day = start; !day.isAfter(end); day = day.plusDays(1)) {
            dailyClicks.add(DailyClicksDto.builder()
                    .date(day.toString())
                    .clicks(clicksByDate.getOrDefault(day, 0L)) // 조회된 값 없으면 0
                    .build());
        }

        // 응답 DTO 조립
        return DashboardDailyClicksResponseDto.builder()
                .start(start.toString())
                .end(end.toString())
                .dailyClicks(dailyClicks)
                .build();
    }

}
