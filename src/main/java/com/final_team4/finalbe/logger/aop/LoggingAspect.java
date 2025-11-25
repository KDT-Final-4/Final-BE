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

  /**
   * 비즈니스 코드가 호출할 필요 없이 AOP가 내부적으로 사용합니다.
   */
  private final ApplicationLogger applicationLogger;

  /**
   * @Loggable이 붙은 메서드 실행 전 지정된 메시지/타입으로 로그를 남깁니다.
   * 개발자가 직접 호출할 일이 없는 AOP 어드바이스입니다.
   */
  @Around("@annotation(loggable)")
  public Object logMethod(ProceedingJoinPoint pjp, Loggable loggable) throws Throwable {
    applicationLogger.log(loggable.value(), loggable.type());
    return pjp.proceed();
  }
}
