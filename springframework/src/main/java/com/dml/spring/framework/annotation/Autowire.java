package com.dml.spring.framework.annotation;


import java.lang.annotation.*;

/**
 * 自动注入
 * @author Tom
 *
 */
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Autowire {
    String value() default "";
}
