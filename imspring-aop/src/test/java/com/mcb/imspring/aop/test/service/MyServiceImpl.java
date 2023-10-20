package com.mcb.imspring.aop.test.service;

import com.mcb.imspring.core.annotation.Service;

/**
 * 如果没有继承接口，会报错 com.sun.proxy.$Proxy21 cannot be cast to xxx
 */
@Service("myService")
public class MyServiceImpl implements IMyService{

    @Override
    public void test() {
        System.out.println("这是一个服务");
    }
}
