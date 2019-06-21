package com.dml.spring.framework.beans;

import lombok.Data;

@Data
public class BeanWrapper {

    private Object wrapperInstance;

    /**
     * 返回代理以后的 class
     * 可能会是个 $proxy0 代理类
     */
    private Class<?> wrapperClass;


    public BeanWrapper(Object instance) {
        this.wrapperInstance = instance;
        this.wrapperClass = instance.getClass();
    }

}
