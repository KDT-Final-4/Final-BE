package com.final_team4.finalbe.schedule.mapper;

import com.final_team4.finalbe.schedule.domain.Schedule;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ScheduleMapper {
    List<Schedule> findAll(@Param("userId") Long userId);
    Schedule findById(@Param("userId")Long userId,@Param("id") Long id);
    void insert(Schedule schedule);
    int deleteById(@Param("userId")Long userId,@Param("id") Long id);
    void update(Schedule schedule);
}
