package com.final_team4.finalbe.schedule.service;

import com.final_team4.finalbe._core.exception.ContentNotFoundException;
import com.final_team4.finalbe._core.exception.PermissionDeniedException;
import com.final_team4.finalbe.schedule.domain.RepeatInterval;
import com.final_team4.finalbe.schedule.domain.Schedule;
import com.final_team4.finalbe.schedule.dto.schedule.*;
import com.final_team4.finalbe.schedule.mapper.ScheduleMapper;
import com.final_team4.finalbe.trend.dto.TrendCreateContentRequestDto;
import com.final_team4.finalbe.trend.service.TrendService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class ScheduleService {

    private final ScheduleMapper scheduleMapper;

    private final ThreadPoolTaskExecutor scheduleExecutor;

    private final TrendService trendService;

    public ScheduleService(ScheduleMapper scheduleMapper, @Qualifier("scheduleExecutor")ThreadPoolTaskExecutor scheduleExecutor, TrendService trendService) {
        this.scheduleMapper = scheduleMapper;
        this.scheduleExecutor = scheduleExecutor;
        this.trendService = trendService;
    }

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

    @Transactional
    public void updateIsActive(Long userId, Long id) {
        Schedule verifiedSchedule = findVerifiedSchedule(userId, id);
        Boolean status = !verifiedSchedule.getIsActive();
        scheduleMapper.updateIsActive(userId, id, status);
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

    @Transactional
    public void processDueSchedules() {
        // 실행 할 스케쥴 찾기
        List<Schedule> dueSchedules = scheduleMapper.findDueSchedules();

        // 각 스케줄 처리
        for (Schedule schedule : dueSchedules) {
            int updated = scheduleMapper.lockSchedule(schedule.getId());
            if (updated == 0) {
                continue; // 이미 누군가 가져간 상태
            }
            try{
                scheduleExecutor.execute(() -> executeSchedule(schedule));
            } catch (Exception e){
                scheduleMapper.unlockSchedule(schedule.getId());
                throw e;
            }
        }

    }

    private void executeSchedule(Schedule schedule) {
        try {
            // 컨텐츠 생성하기 위한 준비
            TrendCreateContentRequestDto requestDto = TrendCreateContentRequestDto.builder()
                    .keyword("")
                    .build();
            // 컨텐츠 생성 로직
            trendService.requestCreateContent(requestDto, schedule.getUserId());
            // 작업 끝난 후 next_execution_at 갱신
            LocalDateTime updateExecutionAt = updateExecutionAt(schedule);
            scheduleMapper.updateNextExecution(schedule.getId(), updateExecutionAt);
        } catch (Exception e) {
            // 실패 시 다음으로 갱신
            updateExecutionAt(schedule);
            throw e;
        } finally {
            // 락 해제
            scheduleMapper.unlockSchedule(schedule.getId());
        }
    }

    private LocalDateTime updateExecutionAt(Schedule schedule) {
        LocalDateTime updateExecutionAt = schedule.getNextExecutionAt();

        switch (schedule.getRepeatInterval()) {
            case RepeatInterval.DAILY:
                updateExecutionAt = updateExecutionAt.plusDays(1);
                break;
            case RepeatInterval.WEEKLY:
                updateExecutionAt = updateExecutionAt.plusWeeks(1);
                break;
            case RepeatInterval.MONTHLY:
                updateExecutionAt = updateExecutionAt.plusMonths(1);
                break;
            case RepeatInterval.YEARLY:
                updateExecutionAt = updateExecutionAt.plusYears(1);
                break;
            default:
                break;

        }
        return updateExecutionAt;
    }
}
