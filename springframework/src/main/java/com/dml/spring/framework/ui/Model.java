package com.dml.spring.framework.ui;

import java.util.Collection;
import java.util.Map;

public interface Model {


    Model addAttribute(String attributeName, Object attributeValue);


    Model addAttribute(Object attributeValue);

    /**
     * Copy all attributes in the supplied {@code Collection} into this
     * {@code Map}, using attribute name generation for each element.
     * @see #addAttribute(Object)
     */
    Model addAllAttributes(Collection<?> attributeValues);

    /**
     * Copy all attributes in the supplied {@code Map} into this {@code Map}.
     * @see #addAttribute(String, Object)
     */
    Model addAllAttributes(Map<String, ?> attributes);

    /**
     * Copy all attributes in the supplied {@code Map} into this {@code Map},
     * with existing objects of the same name taking precedence (i.e. not getting
     * replaced).
     */
    Model mergeAttributes(Map<String, ?> attributes);

    /**
     * Does this model contain an attribute of the given name?
     * @param attributeName the name of the model attribute (never {@code null})
     * @return whether this model contains a corresponding attribute
     */
    boolean containsAttribute(String attributeName);

    /**
     * Return the current set of model attributes as a Map.
     */
    Map<String, Object> asMap();
}
