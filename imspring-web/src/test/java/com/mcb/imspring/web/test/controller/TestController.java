package com.mcb.imspring.web.test.controller;

import com.mcb.imspring.core.annotation.Controller;
import com.mcb.imspring.web.annotation.RequestMapping;

@Controller
@RequestMapping("test")
public class TestController {

    @RequestMapping("/hello")
    public void hello() {
        System.out.println("hello");
    }
}
