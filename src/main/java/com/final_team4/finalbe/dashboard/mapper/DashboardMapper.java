package com.final_team4.finalbe.dashboard.mapper;

import com.final_team4.finalbe.dashboard.dto.DashboardContentSummary;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface DashboardMapper {
    List<DashboardContentSummary> findContentsByUserId(@Param("userId") Long userId);
    List<Long> findProductIdsByUserId(@Param("contentId") Long contentId);
}
