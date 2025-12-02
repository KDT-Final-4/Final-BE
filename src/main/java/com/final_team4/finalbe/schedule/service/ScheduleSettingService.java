package com.final_team4.finalbe.schedule.service;

import com.final_team4.finalbe._core.exception.ContentNotFoundException;
import com.final_team4.finalbe._core.exception.PermissionDeniedException;
import com.final_team4.finalbe.schedule.domain.ScheduleSetting;
import com.final_team4.finalbe.schedule.dto.scheduleSetting.*;
import com.final_team4.finalbe.schedule.mapper.ScheduleSettingMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ScheduleSettingService {
    private final ScheduleSettingMapper scheduleSettingMapper;

    // Create
    @Transactional
    public ScheduleSettingCreateResponseDto create(Long userId, ScheduleSettingCreateRequestDto createRequestDto) {
        ScheduleSetting entity = createRequestDto.toEntity(userId);
        scheduleSettingMapper.insert(entity);
        return ScheduleSettingCreateResponseDto.from(entity);
    }

    // Read(One)
    public ScheduleSettingDetailResponseDto findById(Long userId, Long id) {
        ScheduleSetting entity = findVerifiedSchedule(userId, id);
        return ScheduleSettingDetailResponseDto.from(entity);
    }

    public ScheduleSettingDetailResponseDto findByUserId(Long userId) {
        ScheduleSetting entity = scheduleSettingMapper.findByUserId(userId);
        if (entity == null) {
            throw new ContentNotFoundException("해당 일정을 찾을 수 없습니다.");
        }
        if (!entity.getUserId().equals(userId)) {
            throw new PermissionDeniedException("접근 권한이 없습니다.");
        }
        return ScheduleSettingDetailResponseDto.from(entity);
    }

    // Update
    @Transactional
    public ScheduleSettingUpdateResponseDto update(Long userId, Long id, ScheduleSettingUpdateRequestDto updateRequestDto) {
        ScheduleSetting verifiedScheduleEntity = findVerifiedSchedule(userId, id);
        verifiedScheduleEntity.update(
                updateRequestDto.isRun(),
                updateRequestDto.getMaxDailyRuns(),
                updateRequestDto.getRetryOnFail()
        );
        scheduleSettingMapper.update(verifiedScheduleEntity);
        return ScheduleSettingUpdateResponseDto.from(verifiedScheduleEntity);
    }

    // 권한 및 에러를 검증하고 이상이 없다면 Schedule을 반환합니다.
    private ScheduleSetting findVerifiedSchedule(Long userId, Long id) {
        ScheduleSetting entity = scheduleSettingMapper.findById(userId, id);

        if (entity == null) {
            throw new ContentNotFoundException("해당 일정을 찾을 수 없습니다.");
        }
        if (!entity.getUserId().equals(userId)) {
            throw new PermissionDeniedException("접근 권한이 없습니다.");
        }

        return entity;
    }


}
