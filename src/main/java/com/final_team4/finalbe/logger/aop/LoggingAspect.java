package com.final_team4.finalbe.logger.aop;

import com.final_team4.finalbe.logger.ApplicationLogger;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
@RequiredArgsConstructor
public class LoggingAspect {

  private final ApplicationLogger applicationLogger;

  @Around("@annotation(loggable)")
  public Object logMethod(ProceedingJoinPoint pjp, Loggable loggable) throws Throwable {
    applicationLogger.log(loggable.value(), loggable.type());
    return pjp.proceed();
  }
}
