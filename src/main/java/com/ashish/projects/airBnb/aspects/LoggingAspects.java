package com.ashish.projects.airBnb.aspects;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Around;
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

//    @Before("execution(* com.ashish.projects.airBnb.service.*.*(..))")// all serivce ke api ko chalane pr ye phle console me print ho jayega
//    public void debugProxy(JoinPoint joinPoint) {
//        log.info("Proxy target: {}", joinPoint.getTarget().getClass());
//    }
//
//    @Before("execution(* com.ashish.projects.airBnb.service.*.*(..))")
//    public void logBeforeMethod(JoinPoint joinPoint) {
//        log.info(" Before method execution: {}", joinPoint.getSignature().getName());
//    }
//
//    @After("execution(* com.ashish.projects.airBnb.service.HotelService(..))")// only for hotelservice ke chalne ke baad console me print hoga
//    public void logMethodAfter(JoinPoint joinPoint) {
//        log.info(" after method execution: {}", joinPoint.getSignature().getName());
//    }



//    @Around("execution(* com.ashish.projects.airBnb.service.*.*(..))")
    public Object logBeforeThenAfterMethod(ProceedingJoinPoint joinPoint) throws Throwable {

        long startTime = System.currentTimeMillis();
        log.info(" Before method execution: {}", joinPoint.getSignature().getName());

        Object result = joinPoint.proceed();

        long endTime = System.currentTimeMillis();
        log.info("time taken {} ms", endTime - startTime);
        log.info(" After method execution: {}", joinPoint.getSignature().getName());
        return result;
    }

//    @Before - before mehtod execution
//    @After - after method execution
//    @AfterReturning- after successful return
//    @AfterThrowing - after throwing execution
//    @Around - both before and after method execution

}


