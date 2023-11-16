package com.mcb.imspring.aop.proxy;

/**
 * 在使用 jdk 或者 cglib 创建代理对象的时候，会把 SpringProxy 添加到继承接口里面
 * 后续如果要判断一个对象是否代理对象，可以判断这个对象是否实现了 SpringProxy 接口
 */
public interface SpringProxy {
}
