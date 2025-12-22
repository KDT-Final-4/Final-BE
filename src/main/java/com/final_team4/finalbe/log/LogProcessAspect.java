package com.final_team4.finalbe.log;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Aspect
@Component
public class LogProcessAspect {

    @Around("@annotation(logProcess)")
    public Object logProcess(ProceedingJoinPoint joinPoint, LogProcess logProcess) throws Throwable {
        String action = logProcess.action();
        long startTime = System.currentTimeMillis();

        // 메서드 파라미터 정보 추출
        Map<String, Object> params = extractParameters(joinPoint, logProcess.keys());
        String paramInfo = formatParameters(params);

        // START 로그
        LogMessage.info(LogStatus.START, action, paramInfo);

        try {
            // 실제 메서드 실행
            Object result = joinPoint.proceed();

            // 실행 시간 계산
            long executionTime = System.currentTimeMillis() - startTime;

            // SUCCESS 로그
            if (paramInfo.isBlank()) {
                LogMessage.info(
                        LogStatus.SUCCESS,
                        action,
                        "실행시간={}ms",
                        executionTime
                );
            } else {
                LogMessage.info(
                        LogStatus.SUCCESS,
                        action,
                        "{} 실행시간={}ms",
                        paramInfo,
                        executionTime
                );
            }

            // 성능 경고 (3초 이상 소요시)
            if (executionTime > 3000) {
                LogMessage.warn(LogStatus.SUCCESS, action,
                        "성능 경고 - 실행시간이 {}ms로 임계값(3000ms)을 초과했습니다. {}",
                        executionTime, paramInfo);
            }

            return result;

        } catch (Exception e) {
            // 실행 시간 계산
            long executionTime = System.currentTimeMillis() - startTime;

            // FAIL 로그
            if (paramInfo.isBlank()) {
                LogMessage.error(
                        LogStatus.FAIL,
                        action,
                        e,
                        "실패시간={}ms 예외={}",
                        executionTime,
                        e.getClass().getSimpleName()
                );
            } else {
                LogMessage.error(
                        LogStatus.FAIL,
                        action,
                        e,
                        "{} 실패시간={}ms 예외={}",
                        paramInfo,
                        executionTime,
                        e.getClass().getSimpleName()
                );
            }


            throw e;
        }
    }

    /**
     * 메서드 파라미터에서 지정된 키에 해당하는 값을 추출
     */
    private Map<String, Object> extractParameters(ProceedingJoinPoint joinPoint, String[] keys) {
        Map<String, Object> params = new HashMap<>();

        if (keys == null || keys.length == 0) {
            return params;
        }

        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        Parameter[] parameters = method.getParameters();
        Object[] args = joinPoint.getArgs();

        for (String key : keys) {
            for (int i = 0; i < parameters.length; i++) {
                if (parameters[i].getName().equals(key)) {
                    params.put(key, maskSensitiveData(key, args[i]));
                    break;
                }
            }
        }

        return params;
    }

    /**
     * 파라미터를 로그 출력 형식으로 포맷팅
     */
    private String formatParameters(Map<String, Object> params) {
        if (params.isEmpty()) {
            return "";
        }

        StringBuilder sb = new StringBuilder("[");
        params.forEach((key, value) ->
                sb.append(key).append("=").append(value).append(", ")
        );

        // 마지막 ", " 제거
        if (sb.length() > 1) {
            sb.setLength(sb.length() - 2);
        }

        sb.append("]");
        return sb.toString();
    }

    /**
     * 민감 정보 마스킹 처리
     */
    private Object maskSensitiveData(String key, Object value) {
        if (value == null) {
            return null;
        }

        String lowerKey = key.toLowerCase();

        // 패스워드 관련 필드는 완전히 마스킹
        if (lowerKey.contains("password") || lowerKey.contains("pwd")) {
            return "***";
        }

        // 이메일은 일부만 마스킹
        if (lowerKey.contains("email") && value instanceof String) {
            return maskEmail((String) value);
        }

        // 전화번호는 중간 부분 마스킹
        if (lowerKey.contains("phone") && value instanceof String) {
            return maskPhone((String) value);
        }

        return value;
    }

    private String maskEmail(String email) {
        if (email == null || !email.contains("@")) {
            return email;
        }
        String[] parts = email.split("@");
        String localPart = parts[0];
        if (localPart.length() <= 2) {
            return "*".repeat(localPart.length()) + "@" + parts[1];
        }
        return localPart.substring(0, 2) + "***@" + parts[1];
    }

    private String maskPhone(String phone) {
        if (phone == null || phone.length() < 4) {
            return phone;
        }
        return phone.substring(0, 3) + "****" + phone.substring(phone.length() - 4);
    }
}