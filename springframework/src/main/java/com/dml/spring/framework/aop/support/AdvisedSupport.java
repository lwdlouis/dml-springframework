package com.dml.spring.framework.aop.support;

import com.dml.spring.framework.aop.aspect.AfterReturningAdviceInterceptor;
import com.dml.spring.framework.aop.aspect.AfterThrowingAdviceInterceptor;
import com.dml.spring.framework.aop.aspect.MethodBeforeAdviceInterceptor;
import com.dml.spring.framework.aop.config.AopConfig;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class AdvisedSupport {


    private Class<?> targetClass;

    private Object target;

    private AopConfig config;

    // 把方法和对应的拦截器链关联起来
    private transient Map<Method, List<Object>> methodCache;


    public AdvisedSupport(AopConfig config) {
        this.config = config;
    }


    public List<Object> getInterceptorsAndDynamicInterceptionAdvice(Method method, Class<?> targetClass) throws Exception{
        List<Object> cached = methodCache.get(method);
        if(cached == null){
            Method m = targetClass.getMethod(method.getName(),method.getParameterTypes());

            cached = methodCache.get(m);

            //底层逻辑，对代理方法进行一个兼容处理
            this.methodCache.put(m,cached);
        }

        return cached;
    }


    public boolean pointCutMatch() {
        try {
            return this.targetClass.isAnnotationPresent((Class<? extends Annotation>) Class.forName(config.getPointCut()));
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return false;
    }


    public Class<?> getTargetClass() {
        return targetClass;
    }


    public void setTargetClass(Class<?> targetClass) {
        this.targetClass = targetClass;
        parse();
    }

    /**
     * 主要作用是将切面方法绑定到 methodCache 方法缓存上
     * 1。 key 是该 method， value 是 执行器链
     */
    private void parse() {

        try {
            methodCache = new HashMap<Method, List<Object>>();

            // 获取切面自己定义的所有方法， 缓存起来
            Class aspectClass = Class.forName(this.config.getAspectClass());
            Map<String,Method> aspectMethods = new HashMap<String,Method>();
            for (Method m : aspectClass.getMethods()) {
                aspectMethods.put(m.getName(),m);
            }


            for (Method m : this.targetClass.getMethods()) {
                if (pointCutMatch()) {
                    // 执行器链
                    List<Object> advices = new LinkedList<Object>();

                    //把每一个方法包装成 MethodIterceptor
                    //before
                    if(!(null == config.getAspectBefore() || "".equals(config.getAspectBefore()))) {
                        //创建一个Advivce
                        advices.add(new MethodBeforeAdviceInterceptor(aspectMethods.get(config.getAspectBefore()),  aspectClass.newInstance()));
                    }

                    //after
                    if(!(null == config.getAspectAfter() || "".equals(config.getAspectAfter()))) {
                        //创建一个Advivce
                        advices.add(new AfterReturningAdviceInterceptor(aspectMethods.get(config.getAspectAfter()),aspectClass.newInstance()));
                    }

                    //afterThrowing
                    if(!(null == config.getAspectAfterThrow() || "".equals(config.getAspectAfterThrow()))) {
                        //创建一个Advivce
                        AfterThrowingAdviceInterceptor throwingAdvice =
                                new AfterThrowingAdviceInterceptor(
                                        aspectMethods.get(config.getAspectAfterThrow()),
                                        aspectClass.newInstance());
                        throwingAdvice.setThrowName(config.getAspectAfterThrowingName());
                        advices.add(throwingAdvice);
                    }
                    this.methodCache.put(m, advices);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    public Object getTarget() {
        return target;
    }

    public void setTarget(Object target) {
        this.target = target;
    }


}
