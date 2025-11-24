package com.final_team4.finalbe.logger.aop;

import com.final_team4.finalbe.logger.domain.type.LogType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Loggable {
  String value();
  LogType type() default LogType.INFO;
}
