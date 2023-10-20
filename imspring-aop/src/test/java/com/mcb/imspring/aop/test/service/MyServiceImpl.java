package com.mcb.imspring.aop.test.service;

import com.mcb.imspring.core.annotation.Service;

/**
 * 实现了接口，默认使用jdk代理
 */
@Service("myService")
public class MyServiceImpl implements IMyService {

    @Override
    public void test() {
        System.out.println("这是一个myService");
    }
}
