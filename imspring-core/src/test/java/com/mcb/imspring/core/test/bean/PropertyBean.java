package com.mcb.imspring.core.test.bean;

import com.mcb.imspring.core.annotation.Component;
import com.mcb.imspring.core.annotation.Value;

@Component
public class PropertyBean {
    @Value("111")
    private String num;

    @Value("${mcb.name}")
    private String name;

    @Value("${mcb.age}")
    private String age;

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("PropertyBean{");
        sb.append("num='").append(num).append('\'');
        sb.append(", name='").append(name).append('\'');
        sb.append(", age='").append(age).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
