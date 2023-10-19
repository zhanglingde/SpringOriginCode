package com.ling.test18.populateBean.annotation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class PersonService {

    @Autowired
	// @Resource
    private PersonDao personDao;
}
