package com.mcb.imspring.core.test.bean;

import com.mcb.imspring.core.annotation.Autowired;
import com.mcb.imspring.core.annotation.Component;

@Component
public class ServiceA {

    @Autowired
    private ServiceB serviceB;

    public void test() {
        serviceB.hello();
    }

    public void hello() {
        System.out.println("我是ServiceA，当前bean: " + this + ", serviceB的bean: " + serviceB);
    }
}
