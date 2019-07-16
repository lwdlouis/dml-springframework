package com.dml.spring.framework.context;


import com.dml.spring.framework.annotation.*;
import com.dml.spring.framework.aop.AopPoxy;
import com.dml.spring.framework.aop.CglibAopProxy;
import com.dml.spring.framework.aop.JdkDynamicAopProxy;
import com.dml.spring.framework.aop.config.AopConfig;
import com.dml.spring.framework.aop.support.AdvisedSupport;
import com.dml.spring.framework.beans.BeanFactory;
import com.dml.spring.framework.beans.BeanWrapper;
import com.dml.spring.framework.beans.factory.config.BeanDefinition;
import com.dml.spring.framework.beans.factory.support.BeanDefinitionReader;
import com.dml.spring.framework.beans.factory.support.DefaultListableBeanFactory;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Spring 源码中是 接口， 有多个实现类，
 * 例如 ClassPathXmlApplicationContext (xml 加载bean）,  AnnotationConfigApplicationContext（注解加载 bean）
 * <p>
 * 这里为了方便和简单点，就不弄这么复杂，直接在 ApplicationContext 实现
 * 而 DefaultListableBeanFactory
 * Spring 源码中 子类例如AnnotationConfigApplicationContext
 * 会以 private final DefaultListableBeanFactory beanFactory;  形式运用
 * <p>
 * 套路 ： IOC， DI， AOP
 */
public class ApplicationContext extends DefaultListableBeanFactory implements BeanFactory {


    // 单例的 IOC 容器缓存
    private Map<String, Object> factoryBeanObjectCache = new ConcurrentHashMap<String, Object>();

    //通用的 IOC 容器
    private Map<String, BeanWrapper> factoryBeanInstanceCache = new ConcurrentHashMap<String, BeanWrapper>();

    private String[] configurations;

    private BeanDefinitionReader reader;

    // ClassPathXmlA
    public ApplicationContext(String... configurations) {
        this.configurations = configurations;
        try {
            refresh();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void refresh() throws Exception {
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


    /**
     * getBean() 方法是真正的 DI 操作
     */
    @Override
    public Object getBean(String beanName) {

        // （这是我自己加的）如果已经初始化就返回， 否则初始化
        BeanWrapper beanWrapper = factoryBeanInstanceCache.get(beanName);
        if (null != beanWrapper) {
            return beanWrapper.getWrapperInstance();
        }

        BeanDefinition beanDefinition = this.beanDefinitionMap.get(beanName);

        /**
         * 1. 初始化 bean
         * 可避免循环依赖。 可以先不用管依赖而初始化这个类， 然后再注入，因为
         */
        beanWrapper = this.instantiateBean(beanName, beanDefinition);

        // 2. 拿到 beanWrapper 之后，把 beanWrapper 保存到 IOC 容器去
        this.factoryBeanInstanceCache.put(beanName, beanWrapper);

        // 3。 注入 bean
        this.populateBean(beanName, new BeanDefinition(), beanWrapper);


        return factoryBeanInstanceCache.get(beanName).getWrapperInstance();
    }

    private void populateBean(String beanName, BeanDefinition beanDefinition, BeanWrapper beanWrapper) {

        Object instance = beanWrapper.getWrapperInstance();
        Class<?> clazz = beanWrapper.getWrapperClass();

        // 判断加了注解的类才执行注入
        if (clazz.isAnnotationPresent(Controller.class) || clazz.isAnnotationPresent(Service.class)
                || clazz.isAnnotationPresent(Component.class) || clazz.isAnnotationPresent(RestController.class)) {
            // 获得所有的 fields
            for (Field field : clazz.getDeclaredFields()) {
                if (field.isAnnotationPresent(Autowire.class)) {
                    Autowire autowire = field.getAnnotation(Autowire.class);
                    String autowireBeanName = autowire.value().trim();

                    if ("".equals(autowireBeanName)) {
                        autowireBeanName = field.getName();
                    }

                    field.setAccessible(true);

                    try {

                        Object autowireBeanInstance = null;

                        /**
                         * 如果类里面有其他类属性，先看看有没有初始化，初始化了就直接拿。
                         */
                        if (this.factoryBeanInstanceCache.containsKey(autowireBeanName)) {
                            autowireBeanInstance = this.factoryBeanInstanceCache.get(autowireBeanName).getWrapperInstance();

                        } else {
                            // 如果 autowire 的Bean 还没初始化, 调用 geBean 方法，这里还能解决循环依赖的问题。
                            autowireBeanInstance = getBean(autowireBeanName);
                        }

                        if (null != autowireBeanInstance) {
                            /**
                             *  注入
                             *  1。这个方法的参数意思是， 分析到这个 class 有多少的属性是 @Autowire 的
                             *  2。然后这个属性调用 set 方法， 第一个参数是这个class 的哪个实例，第二个参数是具体的值。
                             *  3。所以你看到具体值是根据 autowire name 来拿的。
                             */
                            field.set(instance, autowireBeanInstance);
                        }


                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }

            }
        }
    }


    /**
     * 1. 读取 BeanDefinition 里面的配置，去做真正的初始化
     * 2. 注册到 真正的 IOC 容器并真正初始化
     */
    private BeanWrapper instantiateBean(String beanName, BeanDefinition beanDefinition) {

        // 1.拿到实例化的对象类名
        String className = beanDefinition.getBeanClassName();


        // 2。反射实例化，得到一个对象
        Object instance = null;
        try {
            // 如果单例的 IOC 容器缓存里面有就直接获取 （单例的情况下）
            if (this.factoryBeanObjectCache.containsKey(className)) {
                instance = this.factoryBeanObjectCache.get(className);
            } else {

                Class<?> clazz = Class.forName(className);
                instance = clazz.newInstance();

                //在类初始化，添加到 IOC 之前，添加 AOP
                AdvisedSupport config = instantionAopConfig(beanDefinition);
                config.setTargetClass(clazz);
                config.setTarget(instance);

                //符合PointCut的规则的话，闯将代理对象
                if(config.pointCutMatch()) {
                    instance = createProxy(config).getProxy();
                }

                //根据类型注入
                this.factoryBeanObjectCache.put(className, instance);
                //根据bean name 注入
                this.factoryBeanObjectCache.put(beanDefinition.getFactoryBeanName(), instance);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        // 3。把这个对象封装到 BeanWrapper
        BeanWrapper beanWrapper = new BeanWrapper(instance);

        return beanWrapper;
    }

    private AopPoxy createProxy(AdvisedSupport config) {
        Class targetClass = config.getTargetClass();
        if(targetClass.getInterfaces().length > 0){
            return new JdkDynamicAopProxy(config);
        }
        return new CglibAopProxy(config);
    }

    private AdvisedSupport instantionAopConfig(BeanDefinition beanDefinition) {
        AopConfig config = new AopConfig();
        config.setPointCut(this.reader.getConfig().getProperty("pointCut"));
        config.setAspectClass(this.reader.getConfig().getProperty("aspectClass"));
        config.setAspectBefore(this.reader.getConfig().getProperty("aspectBefore"));
        config.setAspectAfter(this.reader.getConfig().getProperty("aspectAfter"));
        config.setAspectAfterThrow(this.reader.getConfig().getProperty("aspectAfterThrow"));
        config.setAspectAfterThrowingName(this.reader.getConfig().getProperty("aspectAfterThrowingName"));
        return new AdvisedSupport(config);
    }


    public String[] getBeanDefinitionNames() {
        return this.beanDefinitionMap.keySet().toArray(new String[this.beanDefinitionMap.size()]);
    }


    public int getBeanDefinitionCount() {
        return this.beanDefinitionMap.size();
    }


    public Properties getConfig(){
        return this.reader.getConfig();
    }


}
