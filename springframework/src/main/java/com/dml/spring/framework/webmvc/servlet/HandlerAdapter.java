package com.dml.spring.framework.webmvc.servlet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class HandlerAdapter {

    public boolean supports(Object handler) {
        return (handler instanceof HandlerAdapter);
    }


    ModelAndView handle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {


        return null;
    }

}
