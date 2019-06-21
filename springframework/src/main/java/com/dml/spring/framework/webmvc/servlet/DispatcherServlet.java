package com.dml.spring.framework.webmvc.servlet;

import com.alibaba.fastjson.JSON;
import com.dml.spring.framework.annotation.Controller;
import com.dml.spring.framework.annotation.RequestMapping;
import com.dml.spring.framework.annotation.RestController;
import com.dml.spring.framework.context.ApplicationContext;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.JarURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.regex.Pattern;

@Slf4j
public class DispatcherServlet extends HttpServlet {

    private String CONTEXT_CONFIG_LOCATION = "contextConfigLocation";

    private ApplicationContext context;

    private List<HandlerMapping> handlerMappings;

    //TODO Spring源码逻辑很复杂，至今没看到懂，按课程来先
//    private List<HandlerAdapter> handlerAdapters;
    private Map<HandlerMapping, HandlerAdapter> handlerAdapters;


    private List<ViewResolver> viewResolvers = new ArrayList<ViewResolver>();


    @Override
    public void init(ServletConfig config) throws ServletException {
        //1. 初始化 ApplicationContext
        context = new ApplicationContext(config.getInitParameter(CONTEXT_CONFIG_LOCATION));

        //2. 初始化 spring mvc 九大策略
        try {
            initStrategies(context);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
        doPost(req, resp);
    }


    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) {
        try {
            doDispatch(req, resp);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void doDispatch(HttpServletRequest request, HttpServletResponse response) throws Exception {

        HttpServletRequest processedRequest = request;
        HandlerExecutionChain mappedHandler = null;

        //1、通过从request中拿到URL，去匹配一个HandlerMapping
        // TODO 话说有拦截器的话，是用装饰者模式来一层层包着吗？
        mappedHandler = getHandler(processedRequest);
        // 如果handler为空,则返回404
        if (mappedHandler == null) {
            noHandlerFound(processedRequest, response);
            return;
        }

        //2、准备调用前的参数
        HandlerAdapter ha = getHandlerAdapter(mappedHandler.getHandler());

        //3、真正的调用方法,返回ModelAndView存储了要穿页面上值，和页面模板的名称
        ModelAndView mv = ha.handle(processedRequest, response, mappedHandler.getHandler());

        //这一步才是真正的输出
        processDispatchResult(request, response, mv);

    }

    private void processDispatchResult(HttpServletRequest request, HttpServletResponse response, ModelAndView mv) throws Exception {

        /**
         * 把 modelAndView 变成一个HTML、OuputStream、json、freemark、veolcity
         */


        // 如果有 viewName 的就是输出页面
        if (null != mv.getViewName()) {

            // TODO 其实并不是很了解 viewResolver 的作用。因为这里只有一个
            for (ViewResolver viewResolver : this.viewResolvers) {

                View view = viewResolver.resolveViewName(mv.getViewName(), null);
                view.render(mv.getModel(), request, response);
                return;
            }
        }

        // 如果有 data 输出 json
        else if (null != mv.getData()) {
            response.setCharacterEncoding("utf-8");
            response.getWriter().write(JSON.toJSONString(mv.getData()));
        }
    }

    private void noHandlerFound(HttpServletRequest processedRequest, HttpServletResponse response) {
        log.error("没有找到 handler");
    }


    protected HandlerExecutionChain getHandler(HttpServletRequest request) throws Exception {
        if (this.handlerMappings != null) {
            for (HandlerMapping hm : this.handlerMappings) {
                HandlerExecutionChain handler = hm.getHandler(request);
                if (handler != null) {
                    return handler;
                }
            }
        }
        return null;
    }


    protected HandlerAdapter getHandlerAdapter(Object handler) throws ServletException {

        if (this.handlerAdapters != null) {
//            for (HandlerAdapter ha : this.handlerAdapters) {
//                if (ha.supports(handler)) {
//                    return ha;
//                }
//            }

            HandlerAdapter ha = this.handlerAdapters.get(handler);
            if (ha.supports(handler)) {
                return ha;
            }
        }
        throw new ServletException("No adapter for handler [" + handler +
                "]: The DispatcherServlet configuration needs to include a HandlerAdapter that supports this handler");
    }


    //初始化策略
    protected void initStrategies(ApplicationContext context) throws Exception {
        //多文件上传的组件
//        initMultipartResolver(context);
        //初始化本地语言环境
//        initLocaleResolver(context);
        //初始化模板处理器
//        initThemeResolver(context);

        //handlerMapping
        initHandlerMappings(context);

        //初始化参数适配器
        initHandlerAdapters(context);

        //初始化异常拦截器
        initHandlerExceptionResolvers(context);

        //初始化视图预处理器
//        initRequestToViewNameTranslator(context);

        //初始化视图转换器
        initViewResolvers(context);

        //参数缓存器
//        initFlashMapManager(context);
    }

    private void initViewResolvers(ApplicationContext context) throws Exception {

        //拿到模板的存放目录
        String templateRoot = context.getConfig().getProperty("templateRoot");
        String templateRootPath = this.getClass().getClassLoader().getResource(templateRoot).getFile();

        URL url = this.getClass().getResource("/" + templateRoot);

        String protocol = url.getProtocol();


        if ("file".equals(protocol)) {

            File classPath = new File(url.getFile());

            for (File file : classPath.listFiles()) {
                File templateRootDir = new File(templateRootPath);
                String[] templates = templateRootDir.list();
                for (int i = 0; i < templates.length; i++) {
                    //这里主要是为了兼容多模板，所有模仿Spring用List保存
                    //在我写的代码中简化了，其实只有需要一个模板就可以搞定
                    //只是为了仿真，所有还是搞了个List
                    this.viewResolvers.add(new ViewResolver(templateRoot));
                }

            }
        } else if ("jar".equals(protocol)) {

            JarFile jar = ((JarURLConnection) url.openConnection()).getJarFile();

            Enumeration<JarEntry> entry = jar.entries();

            JarEntry jarEntry;
            String name, className;
            while (entry.hasMoreElements()) {
                // 获取jar里的一个实体 可以是目录 和一些jar包里的其他文件 如META-INF等文
                jarEntry = entry.nextElement();
                name = jarEntry.getName();

                if (!jarEntry.isDirectory() || !name.startsWith(templateRoot)) {
                    continue;
                }
                this.viewResolvers.add(new ViewResolver(templateRoot));
            }
        }
    }

    private void initHandlerExceptionResolvers(ApplicationContext context) {
    }

    /**
     * 把一个 request 请求编程一个 handler，多参数是字符串，自动配到 handler
     */
    private void initHandlerAdapters(ApplicationContext context) {
        this.handlerAdapters = new ConcurrentHashMap<HandlerMapping, HandlerAdapter>();

        for (HandlerMapping hm : this.handlerMappings) {
            this.handlerAdapters.put(hm, new HandlerAdapter());
        }

    }

    private void initHandlerMappings(ApplicationContext context) {

        this.handlerMappings = new ArrayList<HandlerMapping>();

        /**
         * 把 IOC 容器所有的类找出来
         */
        for (String beanName : context.getBeanDefinitionNames()) {
            Object controller = context.getBean(beanName);

            Class<?> controllerClass = controller.getClass();

            /**
             * 1. 判断一下是不是有 @Controller
             * 2. 如果有的话就分析所有的方法，是不是有 @RequestMapping
             * 3. 将 @RequestMapping 的 url 作为 key ， 类名 和 方法名字封装到 handlerMapping
             */
            if (controllerClass.isAnnotationPresent(Controller.class)) {
                for (Method method : controllerClass.getMethods()) {
                    if (method.isAnnotationPresent(RequestMapping.class)) {

                        RequestMapping requestMapping = method.getAnnotation(RequestMapping.class);

                        // 这样是防止 url 的开头，有些加 / 有些不加/
                        String url = requestMapping.value().replace("/+", "/");

                        SimpleHandlerMapping shm = new SimpleHandlerMapping();
                        shm.setController(controller);
                        shm.setMethod(method);
                        shm.setPattern(Pattern.compile(url));

                        handlerMappings.add(shm);
                    }
                }

            }
        }


    }

}
