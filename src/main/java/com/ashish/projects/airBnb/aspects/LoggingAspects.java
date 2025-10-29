package com.ashish.projects.airBnb.aspects;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class LoggingAspects {
    //before cross cutting concerns
    
    @Before("execution(* com.ashish.projects.airBnb.service.*.*(..))")// all serivce ke api ko chalane pr ye phle console me print ho jayega
    public void debugProxy(JoinPoint joinPoint) {
        log.info("Proxy target: {}", joinPoint.getTarget().getClass());
    }

    @Before("execution(* com.ashish.projects.airBnb.service.*.*(..))")
    public void logBeforeMethod(JoinPoint joinPoint) {
        log.info(" Before method execution: {}", joinPoint.getSignature().getName());
    }
}


