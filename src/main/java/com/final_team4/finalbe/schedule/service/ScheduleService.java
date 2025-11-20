package com.final_team4.finalbe.schedule.service;

import com.final_team4.finalbe.schedule.domain.Schedule;
import com.final_team4.finalbe.schedule.dto.ScheduleDetailResponseDto;
import com.final_team4.finalbe.schedule.mapper.ScheduleMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ScheduleService {

    private final ScheduleMapper scheduleMapper;

    public List<ScheduleDetailResponseDto> findAll() {
        List<Schedule> entities = scheduleMapper.findAll();
        return entities.stream().map(ScheduleDetailResponseDto::from).toList();
    }
}
