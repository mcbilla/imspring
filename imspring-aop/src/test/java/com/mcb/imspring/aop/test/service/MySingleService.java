package com.mcb.imspring.aop.test.service;

import com.mcb.imspring.core.annotation.Service;

/**
 * 没有实现接口，默认使用cglib代理
 * 如果强制使用jdk代理，会报错 com.sun.proxy.$Proxy21 cannot be cast to xxx
 */
@Service
public class MySingleService {
    public void test() {
        System.out.println("这是mySingleService的test方法");
    }

    public String test(String str) {
        String res = "这是mySingleService的test方法 " + str;
        System.out.println(res);
        return res;
    }

    public void hello() {
        System.out.println("这是mySingleService的hello方法");
    }
}
