package com.final_team4.finalbe.dashboard.service;


import com.final_team4.finalbe.dashboard.dto.DashboardStatusGetResponseDto;
import com.final_team4.finalbe.dashboard.mapper.ClicksMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DashboardService {

    private final ClicksMapper clicksMapper;


    public DashboardStatusGetResponseDto getStatus() {
        long clicks = clicksMapper.countAllClicks();

        return DashboardStatusGetResponseDto.builder()
                .allClicks(clicks)
                .allViews(0) // 아직  구현할지 미정이고 추후 구현 가능성 때문에 우선 0으로 넣어둠
                .visitors(0)
                .averageDwellTime(0)
                .build();
    }
}
