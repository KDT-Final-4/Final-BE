package com.final_team4.finalbe._core.config.mybatis;

import com.final_team4.finalbe.trend.domain.TrendSnsType;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.apache.ibatis.type.MappedTypes;

@MappedTypes(TrendSnsType.class)
@MappedJdbcTypes(JdbcType.VARCHAR)
public class TrendSnsTypeHandler extends BaseTypeHandler<TrendSnsType> {
  // TREND.SNS_TYPE <-> TrendSnsType 양방향 매핑을 담당하며, 알 수 없는 값은 null 로 처리한다.
  @Override
  // Enum 값을 DB에 저장할 때 호출되어 enum 이름을 그대로 VARCHAR 컬럼에 넣는다.
  public void setNonNullParameter(PreparedStatement ps, int i, TrendSnsType parameter,
      JdbcType jdbcType) throws SQLException {
    ps.setString(i, parameter.name());
  }

  @Override
  // SELECT 결과에서 컬럼명을 기준으로 enum 으로 되돌린다.
  public TrendSnsType getNullableResult(ResultSet rs, String columnName) throws SQLException {
    return TrendSnsType.from(rs.getString(columnName));
  }

  @Override
  // SELECT 결과에서 컬럼 인덱스를 기준으로 enum 으로 되돌린다.
  public TrendSnsType getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
    return TrendSnsType.from(rs.getString(columnIndex));
  }

  @Override
  // 저장 프로시저 호출 결과에서도 동일한 방식으로 enum 을 역직렬화한다.
  public TrendSnsType getNullableResult(CallableStatement cs, int columnIndex)
      throws SQLException {
    return TrendSnsType.from(cs.getString(columnIndex));
  }
}
