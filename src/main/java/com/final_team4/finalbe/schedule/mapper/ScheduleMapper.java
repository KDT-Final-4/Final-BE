package com.final_team4.finalbe.schedule.mapper;

import com.final_team4.finalbe.schedule.domain.Schedule;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ScheduleMapper {
    List<Schedule> findAll();
}
