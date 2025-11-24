package com.final_team4.finalbe._core.config.mybatis;

import com.final_team4.finalbe.logger.domain.type.LogType;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.apache.ibatis.type.MappedTypes;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@MappedTypes(LogType.class)
@MappedJdbcTypes(JdbcType.NUMERIC)
public class LogTypeHandler extends BaseTypeHandler<LogType> {
  /**
   * Enum → 숫자 컬럼 변환. MyBatis가 내부적으로 호출하므로 직접 사용할 일 없습니다.
   */
  @Override
  public void setNonNullParameter(PreparedStatement ps, int i, LogType parameter, JdbcType jdbcType)
      throws SQLException {
    ps.setLong(i, parameter.getId());
  }

  @Override
  public LogType getNullableResult(ResultSet rs, String columnName) throws SQLException {
    long id = rs.getLong(columnName);
    if (rs.wasNull()) {
      return null;
    }
    return LogType.fromId(id);
  }

  @Override
  public LogType getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
    long id = rs.getLong(columnIndex);
    if (rs.wasNull()) {
      return null;
    }
    return LogType.fromId(id);
  }

  @Override
  public LogType getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
    long id = cs.getLong(columnIndex);
    if (cs.wasNull()) {
      return null;
    }
    return LogType.fromId(id);
  }
}
