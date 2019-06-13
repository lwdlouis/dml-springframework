package com.dml.spring.framework.context;


import com.dml.spring.framework.beans.BeanFactory;
import com.dml.spring.framework.beans.factory.config.BeanDefinition;
import com.dml.spring.framework.beans.factory.support.BeanDefinitionReader;
import com.dml.spring.framework.beans.factory.support.DefaultListableBeanFactory;

import java.util.List;
import java.util.Map;

/**
 * Spring 源码中是 接口， 有多个实现类，
 * 例如 ClassPathXmlApplicationContext (xml 加载bean）,  AnnotationConfigApplicationContext（注解加载 bean）
 *
 * 这里为了方便和简单点，就不弄这么复杂，直接在 ApplicationContext 实现
 * 而 DefaultListableBeanFactory
 * Spring 源码中 子类例如AnnotationConfigApplicationContext
 * 会以 private final DefaultListableBeanFactory beanFactory;  形式运用
 *
 * 套路 ： IOC， DI， AOP
 */
public class ApplicationContext extends DefaultListableBeanFactory implements BeanFactory {


    private String[] configurations;

    private BeanDefinitionReader reader;

    // ClassPathXmlA
    public ApplicationContext(String... configurations) {
        this.configurations = configurations;
    }

    @Override
    protected void refresh() {
        // 1。 定位，定位配置文件
        reader = new BeanDefinitionReader(this.configurations);


        // 2。 加载配置文件，扫描相关的类，把他们封装成 BeanDefinition
        List<BeanDefinition> beanDefinitions = reader.loadBeanDefinitions();


        // 3。 注册，把配置信息放到容器里面（伪IOC容器）
        doRegisterBeanDefinition(beanDefinitions);


        // 4。 把不是延时加载的类，提前初始化
        doAutowire();
    }

    private void doAutowire() {

        /**
         * 两种情况：延时加载 和 非延时加载
         */

        for (Map.Entry<String, BeanDefinition> beanDefinitionEntry : super.beanDefinitionMap.entrySet()) {
            String beanName = beanDefinitionEntry.getKey();

            if (!beanDefinitionEntry.getValue().isLazyInit()) {
                getBean(beanName);
            }
        }

    }

    private void doRegisterBeanDefinition(List<BeanDefinition> beanDefinitions) {

        for (BeanDefinition beanDefinition : beanDefinitions) {
            this.beanDefinitionMap.put(beanDefinition.getFactoryBeanName(), beanDefinition);
        }
    }


    @Override
    public Object getBean(String beanName) {

        /**
         * 1. 初始化 bean
         * 可避免循环依赖。 可以先不用管依赖而初始化这个类， 然后再注入，因为
         */
        instantiateBean(beanName, beanDefinition);

        // 2。 注入 bean
        populateBean(beanName,new BeanDefinition(), beanWrapper);


        return null;
    }

}
