package com.dml.spring.framework.webmvc.servlet;

import javax.servlet.http.HttpServletRequest;

public interface HandlerMapping {

    HandlerExecutionChain getHandler(HttpServletRequest request) throws Exception ;
}
