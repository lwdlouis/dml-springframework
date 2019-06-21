package com.dml.spring.framework.webmvc.servlet;

import lombok.Data;

import java.util.Map;

@Data
public class ModelAndView {


    private String viewName;
    private Map<String,?> model;

    // 这个是我自己加，用来输出 json 的
    private Object data;

    public ModelAndView(Object data) { this.data = data; }

    public ModelAndView(String viewName, Map<String, ?> model) {
        this.viewName = viewName;
        this.model = model;

    }


}
