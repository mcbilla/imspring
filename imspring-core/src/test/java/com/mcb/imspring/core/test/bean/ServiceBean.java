package com.mcb.imspring.core.test.bean;

import com.mcb.imspring.core.annotation.Autowired;
import com.mcb.imspring.core.annotation.Service;

@Service
public class ServiceBean {
    private String name;

    private Integer age;

    @Autowired
    private ComponentBean componentBean;

    public ServiceBean(String name, Integer age) {
        this.name = name;
        this.age = age;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public void test() {
        System.out.println("我是一个myServiceBean");
    }
}
