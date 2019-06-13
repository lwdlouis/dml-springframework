package com.dml.spring.framework.beans;


/**
 * 单例工厂的顶层设计
 */
public interface BeanFactory {

    /**
     * 根据 beanName 从 IOC 容器中获得一个实例 Bean
     *
     * 这是一个单例
     *
     */
    Object getBean(String beanName);
}
