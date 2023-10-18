package com.mcb.imspring.aop.test.service;

import com.mcb.imspring.core.annotation.Service;

@Service
public class MyService {

    public void test() {
        System.out.println("这是一个服务");
    }
}
