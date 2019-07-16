package com.dml.aspect;


import com.dml.spring.framework.aop.aspect.JoinPoint;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;

@Slf4j
public class LogAspect {


    public void before(JoinPoint joinPoint) {
        // 整个 aop 链路的参数都以通过 JoinPoint 传递，因为只有一个 JoinPoint
        joinPoint.setUserAttribute("startTime_" + joinPoint.getMethod().getName(),System.currentTimeMillis());

        //这个方法中的逻辑，是由我们自己写的
        log.info("Invoker Before Method!!!" +
                "\nTargetObject:" +  joinPoint.getThis() +
                "\nArgs:" + Arrays.toString(joinPoint.getArguments()));

    }


    public void after(JoinPoint joinPoint) {

        log.info("Invoker After Method!!!" +
                "\nTargetObject:" +  joinPoint.getThis() +
                "\nArgs:" + Arrays.toString(joinPoint.getArguments()));
        long startTime = (Long) joinPoint.getUserAttribute("startTime_" + joinPoint.getMethod().getName());
        long endTime = System.currentTimeMillis();
        System.out.println("use time :" + (endTime - startTime));
        
    }


    public void afterThrowing(JoinPoint joinPoint, Throwable ex) {
        log.info("出现异常" +
                "\nTargetObject:" +  joinPoint.getThis() +
                "\nArgs:" + Arrays.toString(joinPoint.getArguments()) +
                "\nThrows:" + ex.getMessage());
    }


}
