package com.ling.test21.preAop;

import org.springframework.aop.target.AbstractBeanFactoryBasedTargetSource;

public class UserServiceTargetSource extends AbstractBeanFactoryBasedTargetSource {

	private static final long serialVersionUID = 123L;

    @Override
    public Object getTarget() throws Exception {
        return getBeanFactory().getBean(getTargetBeanName());
    }

    @Override
    public boolean isStatic() {
        return true;
    }
}