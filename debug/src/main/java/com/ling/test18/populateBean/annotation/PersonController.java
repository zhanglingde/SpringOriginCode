package com.ling.test18.populateBean.annotation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import javax.annotation.Resource;


@Controller
public class PersonController {
    @Autowired
	// @Resource
    private PersonService personService;
}
