package com.ling.test01.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller

public class HelloController {

    @RequestMapping(path = "/hello")

    public String sayHello(){
        System.out.println("hello");
        System.out.println("热部署234");

        return "success";
    }
}
