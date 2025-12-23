package com.final_team4.finalbe.logger.domain.type;

import lombok.Getter;

import java.util.Arrays;

@Getter
public enum LogType {
  INFO(1L, "INFO"),
  ERROR(2L, "ERROR"),
  WARN(3L, "WARN");

  private final long id;
  private final String label;

  LogType(long id, String label) {
    this.id = id;
    this.label = label;
  }

  /**
   * DB의 type_id 숫자에서 Enum을 역변환합니다. 매칭 실패 시 null.
   */
  public static LogType fromId(Long id) {
    if (id == null) {
      return null;
    }
    return Arrays.stream(values())
        .filter(type -> type.id == id)
        .findFirst()
        .orElse(null);
  }
}
