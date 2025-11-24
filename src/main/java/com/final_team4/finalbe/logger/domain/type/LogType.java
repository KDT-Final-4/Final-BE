package com.final_team4.finalbe.logger.domain.type;

import java.util.Arrays;

public enum LogType {
  INFO(1L, "INFO"),
  ERROR(2L, "ERROR");

  private final long id;
  private final String label;

  LogType(long id, String label) {
    this.id = id;
    this.label = label;
  }

  public long getId() {
    return id;
  }

  public String getLabel() {
    return label;
  }

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
