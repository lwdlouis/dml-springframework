package com.dml.service;

import com.dml.spring.framework.annotation.Autowire;
import com.dml.spring.framework.annotation.Component;

@Component
public class CycleDependenceB {

    @Autowire
    private CycleDependenceA cycleDependenceA;


    public void print() {
        System.out.println("I am CycleDependenceB");
    }

    public void testCycle() {
        this.cycleDependenceA.print();
    }
}
