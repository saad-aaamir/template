package com.application.config.aop;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Aspect
@Component
public class LoggingAspect {

    private static final Logger logger = LoggerFactory.getLogger(LoggingAspect.class);

    // Pointcut that matches all repositories, services, and Web REST endpoints
    @Pointcut("within(@org.springframework.stereotype.Repository *)" +
            " || within(@org.springframework.stereotype.Service *)" +
            " || within(@org.springframework.web.bind.annotation.RestController *)")
    public void springBeanPointcut() {}

    // Pointcut that matches all Spring beans in application's main packages
    @Pointcut("within(com.application.*)" +
            " || within(com.application.*)" +
            " || within(com.application..*)")
    public void applicationPackagePointcut() {}

    @Before("applicationPackagePointcut() && springBeanPointcut()")
    public void logWhenEnteringMethod(JoinPoint joinPoint) {
        String traceId = TracingFilter.getTracingNumber();
        String className = joinPoint.getSignature().getDeclaringType().getSimpleName();
        String methodName = joinPoint.getSignature().getName();
        Object[] args = joinPoint.getArgs();
        String params = filterSensitiveData(args);
        if(traceId != null)
            logger.info("Tracing Number: {} - Entering {}: {}() with params {}", traceId, className, methodName, params);
    }

    @AfterReturning(pointcut = "applicationPackagePointcut() && springBeanPointcut()", returning = "result")
    public void logAfterReturningMethod(JoinPoint joinPoint, Object result) {
        String traceId = TracingFilter.getTracingNumber();
        String className = joinPoint.getSignature().getDeclaringType().getSimpleName();
        String methodName = joinPoint.getSignature().getName();
        String returnValue = filterSensitiveData(result);
        if(traceId != null)
            logger.info("Tracing Number: {} - Exiting {}: {}() with result: {}", traceId, className, methodName, returnValue);
    }

    private String filterSensitiveData(Object data) {
        if (data == null) {
            return "null";
        }
        if (data instanceof Object[]) {
            return Arrays.toString(Arrays.stream((Object[]) data)
                    .map(this::filterSensitiveData)
                    .toArray());
        }
        if (data instanceof List) {
            return Arrays.toString(((List<?>) data).stream()
                    .map(this::filterSensitiveData)
                    .toArray());
        }
        return maskSensitiveData(data.toString());
    }

    private String maskSensitiveData(String data) {
        for (String keyword : getSensitiveKeywords()) {
            Pattern pattern = Pattern.compile("(" + keyword + "=)([^,)}\\s]+)");
            Matcher matcher = pattern.matcher(data);
            StringBuffer sb = new StringBuffer();
            while (matcher.find()) {
                matcher.appendReplacement(sb, matcher.group(1) + "*****");
            }
            matcher.appendTail(sb);
            data = sb.toString();
        }
        data = maskDirectTokens(data);

        return data;
    }

    private String maskDirectTokens(String data) {
        Pattern tokenPattern = Pattern.compile("\\beyJ[\\w-]+\\.[\\w-]+\\.[\\w-]+\\b");
        Matcher matcher = tokenPattern.matcher(data);
        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            matcher.appendReplacement(sb, "*****");
        }
        matcher.appendTail(sb);
        return sb.toString();
    }

    private List<String> getSensitiveKeywords() {
        return List.of("token", "password", "secret", "ssn");
    }

}
