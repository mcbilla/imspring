package com.mcb.imspring.core.test.bean;

import com.mcb.imspring.core.annotation.Component;

@Component
public class ComponentBean {
    private String name;

    private Integer age;

    public ComponentBean(String name, Integer age) {
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
}
