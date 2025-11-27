package com.final_team4.finalbe.logger.mapper;

import com.final_team4.finalbe.logger.domain.Log;
import com.final_team4.finalbe.logger.dto.LogTypeCountRow;
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

  /**
   * 검색어/페이지네이션으로 사용자의 로그를 조회합니다.
   */
  List<Log> findLogs(@Param("userId") Long userId, @Param("search") String search, @Param("limit") int limit, @Param("offset") int offset);

  /**
   * 사용자별 로그 타입 개수를 집계합니다.
   */
  List<LogTypeCountRow> countLogsByType(Long userId);

  /**
   * 특정 jobId의 로그를 id 기준 이후 데이터만 조회합니다.
   */
  List<Log> findByJobIdAfterId(@Param("jobId") String jobId, @Param("fromId") Long fromId);
}
