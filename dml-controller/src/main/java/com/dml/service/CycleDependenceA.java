package com.dml.service;

import com.dml.spring.framework.annotation.Autowire;
import com.dml.spring.framework.annotation.Component;

@Component
public class CycleDependenceA {

    @Autowire
    private CycleDependenceB cycleDependenceB;

    public void print() {
        System.out.println("I am CycleDependenceA");
    }

    public void testCycle() {
        this.cycleDependenceB.print();
    }
}
