package com.ling.test20;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BService {

	@Autowired
	AService aService;
}
