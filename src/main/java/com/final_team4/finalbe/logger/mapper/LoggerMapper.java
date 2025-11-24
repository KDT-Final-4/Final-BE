package com.final_team4.finalbe.logger.mapper;

import com.final_team4.finalbe.logger.domain.Log;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface LoggerMapper {
  void insert(Log log);
  Log findById(Long id);
  List<Log> findRecentLogs(@Param("userId") Long userId, @Param("typeId") Long typeId, @Param("limit") int limit);
  List<Log> findByJobId(Long jobId);
}
