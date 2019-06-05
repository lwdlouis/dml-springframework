package com.dml.spring.framework.webmvc.servlet;

import lombok.Data;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Data
public class SimpleHandlerMapping extends AbstractHandlerMapping {

    private Object controller;	//保存方法对应的实例
    private Method method;		//保存映射的方法
    private Pattern pattern;    //URL的正则匹配


    @Override
    public HandlerMapping getHandlerInternal(HttpServletRequest req) {

        String url = req.getRequestURI();
        String contextPath = req.getContextPath();
        url = url.replace(contextPath, "").replaceAll("/+", "/");

        Matcher matcher = this.getPattern().matcher(url);

        if (matcher.matches()) {
            return this;
        }
        return null;
    }
}
