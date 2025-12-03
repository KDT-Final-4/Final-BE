package com.final_team4.finalbe.schedule.mapper;

import com.final_team4.finalbe.schedule.domain.Schedule;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface ScheduleMapper {
    List<Schedule> findAll( @Param("userId") Long userId );
    Schedule findById(Long id);
    void insert(Schedule schedule);
    void deleteById(@Param("userId")Long userId, @Param("id") Long id);
    void update(Schedule schedule);
    void updateIsActive(@Param("userId") Long userId, @Param("id") Long id, @Param("isActive") Boolean isActive);
    int lockSchedule(@Param("id") Long id);
    int unlockSchedule( @Param("id") Long id);
    List<Schedule> findDueSchedules();
    int updateNextExecution (@Param("id") Long id, @Param("nextExecutionAt") LocalDateTime nextExecutionAt);
}
