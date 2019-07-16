package com.dml.spring.framework.aop;

import com.dml.spring.framework.aop.support.AdvisedSupport;

public class CglibAopProxy implements AopPoxy {


    private AdvisedSupport advise;

    public CglibAopProxy(AdvisedSupport config) {
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
}
