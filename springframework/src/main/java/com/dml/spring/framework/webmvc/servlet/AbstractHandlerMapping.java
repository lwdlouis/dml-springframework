package com.dml.spring.framework.webmvc.servlet;


import javax.servlet.http.HttpServletRequest;

public abstract class AbstractHandlerMapping implements HandlerMapping {


    @Override
    public final HandlerExecutionChain getHandler(HttpServletRequest request) throws Exception {

//        Object handler = getHandlerInternal(request);
//        if (handler == null) {
//            handler = getDefaultHandler();
//        }
//        if (handler == null) {
//            return null;
//        }
//        // Bean name or resolved handler?
//        if (handler instanceof String) {
//            String handlerName = (String) handler;
//            handler = obtainApplicationContext().getBean(handlerName);
//        }

        HandlerMapping handler = getHandlerInternal(request);
        HandlerExecutionChain executionChain = null;

        if (null != handler) {
            executionChain = getHandlerExecutionChain(handler, request);
        }

        return executionChain;
    }

    private HandlerExecutionChain getHandlerExecutionChain(HandlerMapping handler, HttpServletRequest request) {
        return new HandlerExecutionChain(handler);
    }


    protected abstract HandlerMapping getHandlerInternal(HttpServletRequest request);
}
