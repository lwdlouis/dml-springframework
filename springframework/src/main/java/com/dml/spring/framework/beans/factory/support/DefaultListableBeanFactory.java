package com.dml.spring.framework.beans.factory.support;


import com.dml.spring.framework.beans.factory.config.BeanDefinition;
import com.dml.spring.framework.context.AbstractApplicationContext;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 默认实现， Spring IOC 实际上是存放在这了
 *
 */
public class DefaultListableBeanFactory extends AbstractApplicationContext {


    /** Map of bean definition objects, keyed by bean name */
    //存储注册信息的BeanDefinition
    protected final Map<String, BeanDefinition> beanDefinitionMap = new ConcurrentHashMap(256);



}
