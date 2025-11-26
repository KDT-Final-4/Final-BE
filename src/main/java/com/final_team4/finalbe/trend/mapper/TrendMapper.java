package com.final_team4.finalbe.trend.mapper;

import com.final_team4.finalbe.trend.domain.Trend;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface TrendMapper {
    void insert(Trend trend);

    List<Trend> findAll(@Param("limit") int limit, @Param("offset") int offset);
}
