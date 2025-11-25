package com.final_team4.finalbe.logger.aop;

import com.final_team4.finalbe.logger.domain.type.LogType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Loggable {
  /**
   * 로그로 남길 메시지. 메서드 호출 시 자동 기록됩니다.
   */
  String value();
  /**
   * 로그 타입. 지정하지 않으면 INFO.
   */
  LogType type() default LogType.INFO;
}
