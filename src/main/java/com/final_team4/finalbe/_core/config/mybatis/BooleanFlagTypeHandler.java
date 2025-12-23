package com.final_team4.finalbe._core.config.mybatis;

import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.apache.ibatis.type.MappedTypes;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@MappedTypes(Boolean.class)
@MappedJdbcTypes(JdbcType.NUMERIC)
public class BooleanFlagTypeHandler extends BaseTypeHandler<Boolean> {

  // DB insert/update 시 boolean → NUMBER(1)로 변환
  @Override
  public void setNonNullParameter(PreparedStatement ps, int i, Boolean parameter, JdbcType jdbcType)
      throws SQLException {
    ps.setInt(i, parameter ? 1 : 0);
  }

  // 컬럼명을 기준으로 조회한 NUMBER(1)을 boolean으로 변환
  @Override
  public Boolean getNullableResult(ResultSet rs, String columnName) throws SQLException {
    int value = rs.getInt(columnName);
    return !rs.wasNull() && value == 1;
  }

  // 컬럼 인덱스를 기준으로 조회한 NUMBER(1)을 boolean으로 변환
  @Override
  public Boolean getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
    int value = rs.getInt(columnIndex);
    return !rs.wasNull() && value == 1;
  }

  // 프로시저 호출 결과 NUMBER(1)을 boolean으로 변환
  @Override
  public Boolean getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
    int value = cs.getInt(columnIndex);
    return !cs.wasNull() && value == 1;
  }
}
