package com.final_team4.finalbe.trend.mapper;

import com.final_team4.finalbe.trend.domain.Trend;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface TrendMapper {
  void insert(Trend trend);
}
