package com.dml.spring.framework.aop.aspect;

import com.dml.spring.framework.aop.intercept.MethodInterceptor;
import com.dml.spring.framework.aop.intercept.MethodInvocation;

import java.lang.reflect.Method;

public class MethodBeforeAdviceInterceptor extends AbstractAspectAdvice implements MethodInterceptor {

    public MethodBeforeAdviceInterceptor(Method aspectMethod, Object aspectTarget) {
        super(aspectMethod, aspectTarget);
    }

    private void before(Method method,Object[] args,Object target) throws Throwable{
        //传送了给织入参数
        method.invoke(target, args);
    }

    @Override
    public Object invoke(MethodInvocation mi) throws Throwable {

        this.before(mi.getMethod(), mi.getArguments(), mi.getTarget());
        return mi.proceed();
    }
}
