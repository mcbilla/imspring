package com.mcb.imspring.web.test.controller;

import com.mcb.imspring.core.annotation.Controller;
import com.mcb.imspring.web.annotation.RequestMapping;
import com.mcb.imspring.web.annotation.ResponseBody;

@Controller
@RequestMapping("test")
public class TestController {

    @RequestMapping("/hello")
    @ResponseBody
    public String hello() {
        return "hello";
    }
}
