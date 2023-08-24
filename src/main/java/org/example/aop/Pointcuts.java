package org.example.aop;

import org.aspectj.lang.annotation.Pointcut;

public class Pointcuts {

    @Pointcut("execution(public String org.example.controller.*Controller.*(..))")
    public void allEndpoints() {}

    @Pointcut("execution(* org.example.controller.converters.*.*(..))")
    public void allConverters() {}

    @Pointcut("execution(* org.example.service.*Service.*(..))")
    public void allServicesMethods() {}

    @Pointcut("execution(* org.example.security.*.*(..))")
    public void allSecurityMethods() {}

    @Pointcut("execution(* org.example.utils.*.*(..))")
    public void allUtilsMethods() {}
}
