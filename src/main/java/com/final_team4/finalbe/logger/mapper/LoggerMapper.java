package com.final_team4.finalbe.logger.mapper;

import com.final_team4.finalbe.logger.domain.Log;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface LoggerMapper {
  /**
   * 로그를 저장합니다. 서비스 레이어에서만 사용합니다.
   */
  void insert(Log log);
  /**
   * ID로 단일 로그를 조회합니다.
   */
  Log findById(Long id);
  /**
   * userId, typeId 조건으로 최신 N개를 가져와 오래된 순으로 반환합니다.
   */
  List<Log> findRecentLogs(@Param("userId") Long userId, @Param("typeId") Long typeId, @Param("limit") int limit);
  /**
   * jobId로 모든 로그를 조회합니다.
   */
  List<Log> findByJobId(String jobId);
}
