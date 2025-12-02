package com.final_team4.finalbe.dashboard.service;


import com.final_team4.finalbe._core.security.JwtPrincipal;
import com.final_team4.finalbe.dashboard.dto.DashboardContentItemDto;
import com.final_team4.finalbe.dashboard.dto.DashboardContentSummary;
import com.final_team4.finalbe.dashboard.dto.DashboardContentsResponseDto;
import com.final_team4.finalbe.dashboard.dto.DashboardStatusGetResponseDto;
import com.final_team4.finalbe.dashboard.mapper.ClicksMapper;
import com.final_team4.finalbe.dashboard.mapper.DashboardMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;


@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DashboardService {

    private final ClicksMapper clicksMapper;
    private final DashboardMapper dashboardMapper;


    public DashboardStatusGetResponseDto getStatus() {
        long clicks = clicksMapper.countAllClicks();

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


}
