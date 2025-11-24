package com.final_team4.finalbe.schedule.mapper;

import com.final_team4.finalbe.schedule.domain.ScheduleSetting;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;


@Mapper
public interface ScheduleSettingMapper {
//    List<ScheduleSetting> findAll(@Param("userId") Long userId);
//    void delete(@Param("userId")Long userId, @Param("id") Long id);
    void update(ScheduleSetting scheduleSetting);
    ScheduleSetting findById(@Param("userId") Long userId, @Param("id") Long id);
    void insert(ScheduleSetting scheduleSetting);
}
