package com.dml.spring.framework.aop.aspect;

import java.lang.reflect.Method;

public class MethodBeforeAdviceInterceptor extends AbstractAspectAdvice {

    public MethodBeforeAdviceInterceptor(Method aspectMethod, Object aspectTarget) {
        super(aspectMethod, aspectTarget);
    }
}
