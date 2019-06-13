package com.dml.spring.framework.beans.factory.config;


import lombok.Data;

@Data
public class BeanDefinition {

    private String beanClassName;
    private boolean lazyInit = false;
    private String factoryBeanName;

    public BeanDefinition() {

    }

    public BeanDefinition(String beanClassName, String factoryBeanName) {
        this.beanClassName = beanClassName;
        this.factoryBeanName = factoryBeanName;
    }

}
