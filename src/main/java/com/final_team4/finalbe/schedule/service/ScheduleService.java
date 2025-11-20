package com.final_team4.finalbe.schedule.service;

import com.final_team4.finalbe.schedule.domain.Schedule;
import com.final_team4.finalbe.schedule.dto.*;
import com.final_team4.finalbe.schedule.mapper.ScheduleMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ScheduleService {

    private final ScheduleMapper scheduleMapper;

    // Create
    public ScheduleCreateResponseDto insert(ScheduleCreateRequestDto createRequestDto) {
        Schedule entity = createRequestDto.toEntity();
        scheduleMapper.insert(entity);
        return ScheduleCreateResponseDto.from(entity);
    }
    // Read - List
    public List<ScheduleDetailResponseDto> findAll(Long userId) {
        List<Schedule> entities = scheduleMapper.findAll(userId);
        return entities.stream().map(ScheduleDetailResponseDto::from).toList();
    }
    // Read - One
    public ScheduleDetailResponseDto findById(Long userId, Long id) {
        Schedule entity = scheduleMapper.findById(userId, id);
        return ScheduleDetailResponseDto.from(entity);
    }
    // Update
    public ScheduleUpdateResponseDto update(Long userId, Long id, ScheduleUpdateRequestDto updateRequestDto) {
        Schedule entity = scheduleMapper.findById(userId, id);
        if (entity == null) {
            throw new IllegalArgumentException("해당 일정을 찾을 수 없습니다.");
        }
        if (!entity.getUserId().equals(userId)) {
            throw new IllegalArgumentException("접근 권한이 없습니다.");
        }
        entity.update(
                updateRequestDto.getTitle(),
                updateRequestDto.getStartTime(),
                updateRequestDto.getRepeatInterval()
        );
        scheduleMapper.update(entity);
        return ScheduleUpdateResponseDto.from(entity);
    }

    // Delete
    public int deleteById(Long userId, Long id) {
        Schedule byId = scheduleMapper.findById(userId, id);
        if (byId == null) {
            throw new IllegalArgumentException("해당 일정을 찾을 수 없습니다.");
        }
        if (!byId.getUserId().equals(userId)) {
            throw new IllegalArgumentException("접근 권한이 없습니다.");
        }
        return scheduleMapper.deleteById(userId, id);
    }
}
