package com.mcb.imspring.boot.controller;

import com.mcb.imspring.core.annotation.Controller;
import com.mcb.imspring.core.annotation.RequestMapping;
import com.mcb.imspring.core.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Controller
@RequestMapping("test")
public class TestController {
    @RequestMapping("/hello")
    public void hello(HttpServletRequest req, HttpServletResponse resp, @RequestParam("name") String name) throws IOException {
        resp.getWriter().write("hello " + name);
    }
}
