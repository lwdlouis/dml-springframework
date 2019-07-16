package com.dml.spring.framework.aop;

import com.dml.spring.framework.aop.support.AdvisedSupport;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class JdkDynamicAopProxy implements AopPoxy, InvocationHandler {


    private AdvisedSupport advise;

    public JdkDynamicAopProxy(AdvisedSupport config) {
        this.advise = config;
    }


    @Override
    public Object getProxy() {
        return null;
    }

    @Override
    public Object getProxy(ClassLoader classLoader) {
        return null;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        return null;
    }
}
