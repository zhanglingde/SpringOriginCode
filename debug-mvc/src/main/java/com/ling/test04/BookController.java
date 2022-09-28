package com.ling.test04;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;

/**
 * 类上添加了 @SessionAttributes 注解，会对 book，description 为名称的参数和所有 Double 类型的参数使用 SessionAttributes 来缓存
 */
@Controller
@RequestMapping("/book")
@SessionAttributes(value = {"book", "description"}, types = Double.class)
public class BookController {

    private final Log logger = LogFactory.getLog(BookController.class);

    @RequestMapping("/index")
    public String index(Model model) throws Exception {
        // 将参数设置到 Model 的同时也会设置到 SessionAttributes 中
        model.addAttribute("book", "三国演义");
        model.addAttribute("description", "中国四大名著之一");
        model.addAttribute("price", Double.valueOf("98.88"));
        return "redirect:get";
    }

    @RequestMapping("/get")
    public String getBySessionAttributes(@ModelAttribute("book") String book,
                                         ModelMap modelMap,
                                         SessionStatus sessionStatus) {
        logger.info("==========get==========");
        logger.info("get by @ModelAttribute:" + book);
        logger.info("ModelMap:" + modelMap.get("book") + "," + modelMap.get("description") + "," + modelMap.get("price"));
        // 使用该方法通知 SessionAttributes 使用完了，删除缓存
        sessionStatus.setComplete();
        return "redirect:complete";
    }

    @RequestMapping("/complete")
    public String afterComplete(ModelMap modelMap) {
        logger.info("=====afterComplete=======");
        logger.info("ModelMap===" + modelMap.get("book") + "," + modelMap.get("description") + "," + modelMap.get("price"));
        return "index";
    }
}
