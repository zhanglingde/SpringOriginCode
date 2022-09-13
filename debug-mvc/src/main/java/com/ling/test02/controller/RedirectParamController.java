package com.ling.test02.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.FlashMap;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;

/**
 * Redirect 转发时参数的传递
 *
 * @author zhangling
 * @date 2022/9/8 7:42 PM
 */
@Controller
public class RedirectParamController {

    /**
     * 处理完 Post 请求后,重定向到一个 get 请求；redirect 没有参数传递的功能
     * @param attr
     * @return
     * @throws IOException
     */
    @RequestMapping(value = "/submit", method = RequestMethod.POST)
    public String submit(RedirectAttributes attr) throws IOException {
        // 1. 在 redirect 之前将参数写入 OUTPUT_FLASH_MAP_ATTRIBUTE，在 redirect 之后的 handle 中 Spring 自动将参数设置到 model 里
        ((FlashMap) ((ServletRequestAttributes) (RequestContextHolder.getRequestAttributes())).getRequest().getAttribute(DispatcherServlet.OUTPUT_FLASH_MAP_ATTRIBUTE)).put("name ", "张三丰");
        // 2. 方式二：把需要传递的参数设置到 RedirectAttributes
        attr.addFlashAttribute("ordersId", "xxx");
        attr.addAttribute("local", "zh-cn");
        return "redirect:showorders";
    }

    @RequestMapping(value = "/showorders", method = RequestMethod.GET)
    public String showOrders(Model model) throws IOException {
        System.out.println("订单页面");
        String ordersId = (String) model.getAttribute("ordersId");
        String name = (String) model.getAttribute("name");
        System.out.println("ordersId = " + ordersId);
        System.out.println("name = " + name);

        return "orders";
    }
}
