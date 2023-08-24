package org.example.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

@Component
@Aspect
@Slf4j
public class CustomAspect {

    @Around("Pointcuts.allEndpoints()")
    public Object aroundEndpointAdvice(ProceedingJoinPoint joinPoint) {
        return defaultHandlingJoinPoint(joinPoint, "controller", "endpoint");
    }

    @Around("Pointcuts.allConverters()")
    public Object aroundConverterAdvice(ProceedingJoinPoint joinPoint) {
        return defaultHandlingJoinPoint(joinPoint, "converter");
    }

    @Around("Pointcuts.allServicesMethods()")
    public Object aroundServiceMethodAdvice(ProceedingJoinPoint joinPoint) {
        return defaultHandlingJoinPoint(joinPoint, "service");
    }

    @Around("Pointcuts.allSecurityMethods()")
    public Object aroundSecurityMethodAdvice(ProceedingJoinPoint joinPoint) {
        return defaultHandlingJoinPoint(joinPoint, "security");
    }

    @Around("Pointcuts.allUtilsMethods()")
    public Object aroundUtilMethodAdvice(ProceedingJoinPoint joinPoint) {
        return defaultHandlingJoinPoint(joinPoint, "util");
    }

    private Object defaultHandlingJoinPoint(JoinPoint joinPoint, String classLayer) {
        return defaultHandlingJoinPoint(joinPoint, classLayer, "method");
    }

    private Object defaultHandlingJoinPoint(JoinPoint joinPoint, String classLayer, String methodCategory) {

        var signature = (MethodSignature) joinPoint.getSignature();

        String message = String.format("%s '%s', %s '%s'",
                classLayer,
                signature.getMethod().getDeclaringClass().getName(),
                methodCategory,
                signature.getName()
        );

        Object result = null;

        try {
            if (joinPoint instanceof ProceedingJoinPoint proceedingJoinPoint) {
                log.info(message + " is invoked");
                result = proceedingJoinPoint.proceed();
                log.info(message + " is completed");
            } else {
                log.info(message + " is invoked");
            }
        } catch (RuntimeException e) {
            log.error(message + " has thrown an error");
            throw e;
        } catch (Throwable e) {
            log.error(message + " has thrown an error");
            throw new RuntimeException(e);
        }

        return result;
    }
}
