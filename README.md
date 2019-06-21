# 手写 springframework 

### 咕泡学院 课程
30 个类纯手写 spring 框架核心功能，个人实践版。



#### ApplicationContext 初始化过程 
    IOC 流程 
    1） 确定配置文件（xml，@Configuration，@SpringBootApplication）
    2） 调用 refresh() 方法
    3） 扫描需要所有的类，找出所有需要初始化的类（@Bean，@Service 等等）
    4） 封装成 BeanDefinition， 放到 IOC 注册器（beanDefinitionMap，只是存着注册信息）
    
    DI 流程
    5） 调用 getBean() 方法
    6） 循环所有需要初始化的Bean（beanDefinitionMap）
    7） 初始化这些Bean并包装成 BeanWrapper，放到 IOC 容器（factoryBeanInstanceCache 等）
    8） 分析这些 Bean 里面的参数是否需要依赖注入 （@Autowire）
    9） 如果需要注入先从 IOC 容器查找是否被初始化了，如果是直接拿，没有则调用 getBean() 方法。
    10）利用反射完成依赖注入。 
    

 #### SpringMVC 工作流程
    
    
