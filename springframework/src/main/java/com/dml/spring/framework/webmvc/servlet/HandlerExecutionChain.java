package com.dml.spring.framework.webmvc.servlet;

import lombok.Data;

import java.util.List;

@Data
public class HandlerExecutionChain {

//    private final Object handler;
    private final HandlerMapping handler;


    private HandlerInterceptor[] interceptors;


    private List<HandlerInterceptor> interceptorList;


    public HandlerExecutionChain(HandlerMapping handler) {
        this.handler = handler;
    }
}
