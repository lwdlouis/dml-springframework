package com.dml.spring.framework.aop.support;

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

    private transient Map<Method, List<Object>> methodCache;


    public AdvisedSupport(AopConfig config) {
        this.config = config;
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




                }


            }






        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    public void setTargetClass(Class<?> targetClass) {
        this.targetClass = targetClass;
    }

    public Object getTarget() {
        return target;
    }

    public void setTarget(Object target) {
        this.target = target;
    }


}
