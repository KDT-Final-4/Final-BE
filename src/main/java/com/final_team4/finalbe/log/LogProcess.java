package com.final_team4.finalbe.log;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface LogProcess {
    /**
     * 로그에 출력될 행위명
     * 예) "사용자 조회", "결제 처리", "스케줄 실행"
     */
    String action();

    /**
     * key=value 형태로 출력할 식별자 필드명
     * 예) {"userId", "orderId"}
     */
    String[] keys() default {};
}
