package com.mcb.imspring.aop.test.service;

import com.mcb.imspring.core.annotation.Service;

/**
 * 没有实现接口，默认使用cglib代理
 * 如果强制使用jdk代理，会报错 com.sun.proxy.$Proxy21 cannot be cast to xxx
 */
@Service
public class MySingleService {
    public void test() {
        System.out.println("这是一个mySingleService");
    }
}