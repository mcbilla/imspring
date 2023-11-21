package com.mcb.imspring.core.test.bean;

import com.mcb.imspring.core.annotation.Autowired;
import com.mcb.imspring.core.annotation.Component;

@Component
public class ServiceB {

    @Autowired
    private ServiceA serviceA;

    public void test() {
        serviceA.hello();
    }

    public void hello() {
        System.out.println("我是ServiceB，当前bean: " + this + ", serviceA的bean: " + serviceA);
    }
}
