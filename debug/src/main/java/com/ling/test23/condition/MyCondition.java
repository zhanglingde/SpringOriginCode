package com.ling.test23.condition;

import org.springframework.context.annotation.ConditionContext;
import org.springframework.context.annotation.ConfigurationCondition;
import org.springframework.core.type.AnnotatedTypeMetadata;

public class MyCondition implements ConfigurationCondition {
	@Override
	public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
		// 判断容器中是否存在名称为 a 的 bean
		return context.getBeanFactory().containsBean("AConfig");
	}

	@Override
	public ConfigurationPhase getConfigurationPhase() {
		// 加载配置类的时候判断是否满足
		return ConfigurationPhase.PARSE_CONFIGURATION;
	}
}
