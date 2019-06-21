package com.dml.spring.framework.ui;

import com.dml.spring.framework.annotation.Component;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class ConcurrentModel extends ConcurrentHashMap<String, Object> implements Model {

    @Override
    public Model addAttribute(String attributeName, Object attributeValue) {
        put(attributeName, attributeValue);
        return this;
    }

}
