package com.dml.spring.framework.aop.intercept;

public interface MethodInterceptor {
    Object invoke(MethodInvocation invocation) throws Throwable;

}
