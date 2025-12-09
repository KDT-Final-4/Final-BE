package com.final_team4.finalbe.dashboard.mapper;

import com.final_team4.finalbe.dashboard.dto.DashboardContentSummary;
import com.final_team4.finalbe.dashboard.dto.DashboardContentsCountResponseDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface DashboardMapper {
    List<DashboardContentSummary> findContentsByUserId(@Param("userId") Long userId);

    Long countAllContentsByUserId(@Param("userId") Long userId);
}
