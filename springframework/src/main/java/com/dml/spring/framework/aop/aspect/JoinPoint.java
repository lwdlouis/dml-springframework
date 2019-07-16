package com.dml.spring.framework.aop.aspect;

import java.lang.reflect.Method;

public interface JoinPoint {


    String getThis();

    long[] getArguments();

    Method getMethod();

    void setUserAttribute(String key, Object value);

    Object getUserAttribute(String key);
}
