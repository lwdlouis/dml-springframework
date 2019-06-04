package com.dml.spring.framework.ui;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ConcurrentModel extends ConcurrentHashMap<String, Object> implements Model {


    public Model addAttribute(String attributeName, Object attributeValue) {
        put(attributeName, attributeValue);
        return this;
    }

    public Model addAttribute(Object attributeValue) {
        return null;
    }

    public Model addAllAttributes(Collection<?> attributeValues) {
        return null;
    }

    public Model addAllAttributes(Map<String, ?> attributes) {
        return null;
    }

    public Model mergeAttributes(Map<String, ?> attributes) {
        return null;
    }

    public boolean containsAttribute(String attributeName) {
        return false;
    }

    public Map<String, Object> asMap() {
        return null;
    }
}
