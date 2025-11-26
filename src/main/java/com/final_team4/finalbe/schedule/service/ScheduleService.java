package com.final_team4.finalbe.schedule.service;

import com.final_team4.finalbe._core.exception.ContentNotFoundException;
import com.final_team4.finalbe._core.exception.PermissionDeniedException;
import com.final_team4.finalbe.schedule.domain.Schedule;
import com.final_team4.finalbe.schedule.dto.schedule.*;
import com.final_team4.finalbe.schedule.mapper.ScheduleMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ScheduleService {

    private final ScheduleMapper scheduleMapper;

    // Create
    @Transactional
    public ScheduleCreateResponseDto insert(Long userId, ScheduleCreateRequestDto createRequestDto) {
        Schedule entity = createRequestDto.toEntity(userId);
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
        Schedule entity = findVerifiedSchedule(userId, id);
        return ScheduleDetailResponseDto.from(entity);
    }

    // Update
    @Transactional
    public ScheduleUpdateResponseDto update(Long userId, Long id, ScheduleUpdateRequestDto updateRequestDto) {
        Schedule entity = findVerifiedSchedule(userId, id);

        entity.update(
                updateRequestDto.getTitle(),
                updateRequestDto.getStartTime(),
                updateRequestDto.getRepeatInterval()
        );

        scheduleMapper.update(entity);
        return ScheduleUpdateResponseDto.from(entity);
    }

    // Delete
    @Transactional
    public void deleteById(Long userId, Long id) {
        Schedule verifiedSchedule = findVerifiedSchedule(userId, id);
        scheduleMapper.deleteById(userId, verifiedSchedule.getId());
    }

    // 권한 및 에러를 검증하고 이상이 없다면 Schedule을 반환합니다.
    private Schedule findVerifiedSchedule(Long userId, Long id) {
        Schedule entity = scheduleMapper.findById(id);

        if (entity == null) {
            throw new ContentNotFoundException("해당 일정을 찾을 수 없습니다.");
        }
        if (!entity.getUserId().equals(userId)) {
            throw new PermissionDeniedException("접근 권한이 없습니다.");
        }

        return entity;
    }

}
