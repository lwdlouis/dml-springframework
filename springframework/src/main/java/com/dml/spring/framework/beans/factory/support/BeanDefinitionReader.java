package com.dml.spring.framework.beans.factory.support;

import com.dml.spring.framework.annotation.Component;
import com.dml.spring.framework.annotation.Controller;
import com.dml.spring.framework.annotation.RestController;
import com.dml.spring.framework.annotation.Service;
import com.dml.spring.framework.beans.factory.config.BeanDefinition;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.JarURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class BeanDefinitionReader {


    /**
     *  spring 源码实现， load 完之后不知道怎么的就放到了 BeanDefinitionRegistry 里面
     *  然后通过里面的 BeanDefinition getBeanDefinition(String beanName) 获得 BeanDefinition
     */
//    public int loadBeanDefinitions(String... locations) throws Exception {
//
//        return 0;
//    }

    /**
     * 乞丐版简化， 直接返回 List<BeanDefinition>
     */
    private List<String> registyBeanClasses = new ArrayList<String>();

    private Properties config = new Properties();

    //固定配置文件中的key，相对于xml的规范
    private final String SCAN_PACKAGE = "scanPackage";


    public BeanDefinitionReader(String... locations) throws Exception {
        //通过URL定位找到其所对应的文件，然后转换为文件流 （如果是注解式就扫描所有的类，scan方法）
        InputStream is = this.getClass().getClassLoader().getResourceAsStream(locations[0].replace("classpath:", ""));
        try {
            config.load(is);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (null != is) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        // 实际上就是获取配置文件后， 根据配置文件的设置，扫描包里的类。
        // （如果是 springboot 就是通过注解，扫描 XXXApplication 类所在的包和子包所有的类）
        doScanner(config.getProperty(SCAN_PACKAGE));
    }

    private void doScanner(String scanPackage) throws Exception {

        // 将包路径 转为 实际文件路径
        URL url = this.getClass().getResource("/" + scanPackage.replaceAll("\\.", "/"));

        String protocol = url.getProtocol();

        if ("file".equals(protocol)) {
            File classPath = new File(url.getFile());

            for (File file : classPath.listFiles()) {
                if (file.isDirectory()) {
                    doScanner(scanPackage + "." + file.getName());
                } else {
                    if (!file.getName().endsWith(".class")) {
                        continue;
                    }
                    String className = (scanPackage + "." + file.getName().replace(".class", ""));
                    registyBeanClasses.add(className);
                }
            }

        } else if ("jar".equals(protocol)) {
            String pkgDir = scanPackage.replace(".", "/");

            JarFile jar = ((JarURLConnection) url.openConnection()).getJarFile();

            Enumeration<JarEntry> entry = jar.entries();

            JarEntry jarEntry;
            String name, className;
            while (entry.hasMoreElements()) {

                // 获取jar里的一个实体 可以是目录 和一些jar包里的其他文件 如META-INF等文
                jarEntry = entry.nextElement();
                name = jarEntry.getName();

                if (jarEntry.isDirectory() || !name.startsWith(pkgDir) || !name.endsWith(".class")) {
                    continue;
                }

                className = name.substring(0, name.length() - 6).replace("/", ".");
                registyBeanClasses.add(className);
            }
        }
    }


    public Properties getConfig() {
        return this.config;
    }


    //把配置文件中扫描到的所有的配置信息转换为GPBeanDefinition对象，以便于之后IOC操作方便
    public List<BeanDefinition> loadBeanDefinitions() {

        List<BeanDefinition> result = new ArrayList<BeanDefinition>();
        try {
            for (String className : registyBeanClasses) {

                Class<?> beanClass = Class.forName(className);

                if (null == beanClass) {
                    continue;
                }

                // 有可能是一个接口，用它的实现类作为beanClassName
                if (beanClass.isInterface()) {
                    continue;
                }

                if (beanClass.isAnnotationPresent(Controller.class)
                        || beanClass.isAnnotationPresent(Service.class)
                        || beanClass.isAnnotationPresent(Component.class)
                        || beanClass.isAnnotationPresent(RestController.class)) {

                    result.add(doCreateBeanDefinition(beanClass));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }


    private BeanDefinition doCreateBeanDefinition(Class beanClass) {
        return new BeanDefinition(beanClass.getName(), toLowerFirstCase(beanClass.getSimpleName()));
    }


    //如果类名本身是小写字母，确实会出问题
    //但是我要说明的是：这个方法是我自己用，private的
    //传值也是自己传，类也都遵循了驼峰命名法
    //默认传入的值，存在首字母小写的情况，也不可能出现非字母的情况

    //为了简化程序逻辑，就不做其他判断了，大家了解就OK
    //其实用写注释的时间都能够把逻辑写完了
    private String toLowerFirstCase(String simpleName) {
        char[] chars = simpleName.toCharArray();
        //之所以加，是因为大小写字母的ASCII码相差32，
        // 而且大写字母的ASCII码要小于小写字母的ASCII码
        //在Java中，对char做算学运算，实际上就是对ASCII码做算学运算
        chars[0] += 32;
        return String.valueOf(chars);
    }


}
