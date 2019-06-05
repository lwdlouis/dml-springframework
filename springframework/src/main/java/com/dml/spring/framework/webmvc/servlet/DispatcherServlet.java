package com.dml.spring.framework.webmvc.servlet;

import com.dml.spring.framework.context.ApplicationContext;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class DispatcherServlet extends HttpServlet {

//    private ApplicationContext context;

    private List<HandlerMapping> handlerMappings;

    //TODO Spring源码逻辑很复杂，至今没看到懂，按课程来先
//    private List<HandlerAdapter> handlerAdapters;
    private Map<HandlerMapping, HandlerAdapter> handlerAdapters;


    @Override
    public void init(ServletConfig config) throws ServletException {
        //1. 初始化 ApplicationContext

        //2. 初始化 spring mvc 九大策略


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

    private void processDispatchResult(HttpServletRequest request, HttpServletResponse response, ModelAndView mv) {
    }

    private void noHandlerFound(HttpServletRequest processedRequest, HttpServletResponse response) {
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
    protected void initStrategies(ApplicationContext context) {
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
//        initViewResolvers(context);
        //
//        initFlashMapManager(context);
    }

    private void initHandlerExceptionResolvers(ApplicationContext context) {
    }

    private void initHandlerAdapters(ApplicationContext context) {
        this.handlerAdapters = new ConcurrentHashMap<HandlerMapping, HandlerAdapter>();

    }

    private void initHandlerMappings(ApplicationContext context) {
        this.handlerMappings = new ArrayList<HandlerMapping>();
    }

}
