package com.ling.test01;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class GoController implements EnvironmentAware {

    private final Log logger = LogFactory.getLog(GoController.class);

    private Environment environment = null;

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    @RequestMapping(value = "/",method = RequestMethod.HEAD)
    public String head(){
        return "index.jsp";
    }

    @GetMapping(value = {"/index"})
    public String index(Model model) {
        logger.info("=========start=========");
        // 这里设置断点,通过 Aware 获取 Envirment

        model.addAttribute("msg", "go go go!");
        return "index.jsp";
    }
}
