/*
 * Copyright 2002-2020 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.context.support;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.support.MergedBeanDefinitionPostProcessor;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.core.OrderComparator;
import org.springframework.core.Ordered;
import org.springframework.core.PriorityOrdered;
import org.springframework.lang.Nullable;

/**
 * Delegate for AbstractApplicationContext's post-processor handling.
 *
 * @author Juergen Hoeller
 * @author Sam Brannen
 * @since 4.0
 */
final class PostProcessorRegistrationDelegate {

	private PostProcessorRegistrationDelegate() {
	}


	/**
	 * 执行 BeanFactory 后置处理器
	 *
	 * @param beanFactory
	 * @param beanFactoryPostProcessors 默认为空，可以自己添加值
	 */
	public static void invokeBeanFactoryPostProcessors(
			ConfigurableListableBeanFactory beanFactory, List<BeanFactoryPostProcessor> beanFactoryPostProcessors) {

		// Invoke BeanDefinitionRegistryPostProcessors first, if any.
		// 无论是什么情况，优先执行 BeanDefinitionRegistryPostProcessors
		// 存储已经执行过的 BeanFactoryPostProcessor，防止重复执行
		Set<String> processedBeans = new HashSet<>();

		// 1. BeanDefinitionRegistry 类型处理（ DefaultListableBeanFactory 为子类）
		if (beanFactory instanceof BeanDefinitionRegistry) {
			BeanDefinitionRegistry registry = (BeanDefinitionRegistry) beanFactory;
			// BeanDefinitionRegistryPostProcessor 是 BeanDefinitionRegistry 的子集
			// BeanFactoryPostProcessor 针对的操作对象是 BeanFactory，BeanDefinitionRegistryPostProcessor 针对的操作对象是 BeanDefinition
			List<BeanFactoryPostProcessor> regularPostProcessors = new ArrayList<>();
			List<BeanDefinitionRegistryPostProcessor> registryProcessors = new ArrayList<>();

			// 2. 首先处理入参中的 beanFactoryPostProcessors
			// 遍历所有 beanFactoryPostProcessors，将 BeanDefinitionRegistryPostProcessor 和 BeanFactoryPostProcessor 区分开
			for (BeanFactoryPostProcessor postProcessor : beanFactoryPostProcessors) {
				if (postProcessor instanceof BeanDefinitionRegistryPostProcessor) {
					BeanDefinitionRegistryPostProcessor registryProcessor =
							(BeanDefinitionRegistryPostProcessor) postProcessor;
					// 直接执行 BeanDefinitionRegistryPostProcessor 接口中的 postProcessBeanDefinitionRegistry 方法
					registryProcessor.postProcessBeanDefinitionRegistry(registry);
					// 添加到 registryProcessors，用于后续执行 postProcessBeanFactory 方法
					registryProcessors.add(registryProcessor);
				}
				else {
					regularPostProcessors.add(postProcessor);
				}
			}

			// Do not initialize FactoryBeans here: We need to leave all regular beans
			// uninitialized to let the bean factory post-processors apply to them!
			// Separate between BeanDefinitionRegistryPostProcessors that implement
			// PriorityOrdered, Ordered, and the rest.
			// 用于保存本次要执行的 BeanDefinitionRegistryPostProcessor
			List<BeanDefinitionRegistryPostProcessor> currentRegistryProcessors = new ArrayList<>();

			// First, invoke the BeanDefinitionRegistryPostProcessors that implement PriorityOrdered.
			// 先执行子类 PriorityOrdered 排序的
			// 调用所有实现 PriorityOrdered 接口的 BeanDefinitionRegistryPostProcessor 实现类
			// 找到所有实现 BeanDefinitionRegistryPostProcessor 接口 bean 的beanName
			String[] postProcessorNames =
					beanFactory.getBeanNamesForType(BeanDefinitionRegistryPostProcessor.class, true, false);
			// 遍历所有符合规则的 postProcessorNames
			for (String ppName : postProcessorNames) {
				// 检测是否实现了 PriorityOrdered 接口
				if (beanFactory.isTypeMatch(ppName, PriorityOrdered.class)) {
					// 获取名字对应的 bean 实例，添加到 currentRegistryProcessors 中
					currentRegistryProcessors.add(beanFactory.getBean(ppName, BeanDefinitionRegistryPostProcessor.class));
					// 将要被执行的 BFPP 添加到 processedBeans，避免后续重复执行
					processedBeans.add(ppName);
				}
			}
			// 按照优先级进行排序
			sortPostProcessors(currentRegistryProcessors, beanFactory);
			// 添加到 registryProcessors 中，用于最后执行 postProcessBeanFactory 方法
			registryProcessors.addAll(currentRegistryProcessors);
			// 遍历 currentRegistryProcessors，执行 postProcessBeanDefinitionRegistry 方法（ConfigurationClassPostProcessor 处理的入口）
			invokeBeanDefinitionRegistryPostProcessors(currentRegistryProcessors, registry);
			// 执行完毕后，清空 currentRegistryProcessors
			currentRegistryProcessors.clear();

			// Next, invoke the BeanDefinitionRegistryPostProcessors that implement Ordered.
			// 后执行父类 Ordered 排序的
			// 调用所有实现 Ordered 接口的 BeanDefinitionRegistryPostProcessor 实现类
			// 找到所有实现 BeanDefinitionRegistryPostProcessor 接口bean 的 beanName，
			// 此处需要重复查找的原因在于上面执行过程中可能会新增其他的 BeanDefinitionRegistryPostProcessor
			postProcessorNames = beanFactory.getBeanNamesForType(BeanDefinitionRegistryPostProcessor.class, true, false);
			for (String ppName : postProcessorNames) {
				// 检测是否实现 Ordered 接口，并且还未被执行过
				if (!processedBeans.contains(ppName) && beanFactory.isTypeMatch(ppName, Ordered.class)) {
					// 获取名字对应的 bean 实例，添加到 currentRegistryProcessors 中
					currentRegistryProcessors.add(beanFactory.getBean(ppName, BeanDefinitionRegistryPostProcessor.class));
					// 将要被执行的 BFPP 添加到 processedBeans，避免后续重复执行
					processedBeans.add(ppName);
				}
			}
			// 按照优先级进行排序
			sortPostProcessors(currentRegistryProcessors, beanFactory);
			registryProcessors.addAll(currentRegistryProcessors);
			invokeBeanDefinitionRegistryPostProcessors(currentRegistryProcessors, registry);
			currentRegistryProcessors.clear();

			// Finally, invoke all other BeanDefinitionRegistryPostProcessors until no further ones appear.
			// 最后执行无序的
			boolean reiterate = true;
			while (reiterate) {
				reiterate = false;
				postProcessorNames = beanFactory.getBeanNamesForType(BeanDefinitionRegistryPostProcessor.class, true, false);
				for (String ppName : postProcessorNames) {
					if (!processedBeans.contains(ppName)) {
						currentRegistryProcessors.add(beanFactory.getBean(ppName, BeanDefinitionRegistryPostProcessor.class));
						processedBeans.add(ppName);
						reiterate = true;
					}
				}
				sortPostProcessors(currentRegistryProcessors, beanFactory);
				registryProcessors.addAll(currentRegistryProcessors);
				// 遍历 currentRegistryProcessors，执行 postProcessBeanDefinitionRegistry 方法
				invokeBeanDefinitionRegistryPostProcessors(currentRegistryProcessors, registry);
				currentRegistryProcessors.clear();
			}

			// Now, invoke the postProcessBeanFactory callback of all processors handled so far.
			// 调用所有 BeanDefinitionRegistryPostProcessor 的 ProcessBeanFactory方法
			invokeBeanFactoryPostProcessors(registryProcessors, beanFactory);
			// 最后，调用入参 beanFactoryPostProcessors 中的 普通 BeanFactoryPostProcessor 的 postProcessBeanFactory 方法
			invokeBeanFactoryPostProcessors(regularPostProcessors, beanFactory);
		}

		else {
			// Invoke factory processors registered with the context instance.
			// 如果 beanFactory 不归属于 BeanDefinitionRegistry 类型，那么直接执行 postProcessBeanFactory 方法
			invokeBeanFactoryPostProcessors(beanFactoryPostProcessors, beanFactory);
		}

		// 到这里为止，入参 beanFactoryPostProcessors 和容器中所有 BeanDefinitionRegistryPostProcessors 已全部处理完毕，接下来处理容器中所有的 BeanFactoryPostProcessors

		// Do not initialize FactoryBeans here: We need to leave all regular beans
		// uninitialized to let the bean factory post-processors apply to them!
		// 找到所有实现 BeanFactoryPostProcessor 接口的类
		String[] postProcessorNames =
				beanFactory.getBeanNamesForType(BeanFactoryPostProcessor.class, true, false);

		// Separate between BeanFactoryPostProcessors that implement PriorityOrdered,
		// Ordered, and the rest.
		// 用于存放实现了 PriorityOrdered 接口的 BeanFactoryPostProcessors
		List<BeanFactoryPostProcessor> priorityOrderedPostProcessors = new ArrayList<>();
		// 用于存放实现了 Ordered 接口的 BeanFactoryPostProcessor 的 beanName
		List<String> orderedPostProcessorNames = new ArrayList<>();
		// 用于存放普通 BeanFactoryPostProcessor 的 beanName
		List<String> nonOrderedPostProcessorNames = new ArrayList<>();
		// 遍历 postProcessorNames,将 BeanFactoryPostProcessor 按实现 PriorityOrdered、实现 Ordered 接口、普通三种区分开
		for (String ppName : postProcessorNames) {
			// 跳过已经执行过的 BeanFactoryPostProcessor
			if (processedBeans.contains(ppName)) {
				// skip - already processed in first phase above
			}
			// 添加实现了 PriorityOrdered 接口的 BeanFactoryPostProcessor
			else if (beanFactory.isTypeMatch(ppName, PriorityOrdered.class)) {
				priorityOrderedPostProcessors.add(beanFactory.getBean(ppName, BeanFactoryPostProcessor.class));
			}
			// 添加实现了 Ordered 接口的 BeanFactoryPostProcessor 的 beanName
			else if (beanFactory.isTypeMatch(ppName, Ordered.class)) {
				orderedPostProcessorNames.add(ppName);
			}
			else {
				// 添加普通无序的 BeanFactoryPostProcessor 的 beanName
				nonOrderedPostProcessorNames.add(ppName);
			}
		}

		// First, invoke the BeanFactoryPostProcessors that implement PriorityOrdered.
		// 对实现 PriorityOrdered 接口的 BeanFactoryPostProcessor 进行排序，遍历并进行调用 postProcessBeanFactory 方法
		sortPostProcessors(priorityOrderedPostProcessors, beanFactory);
		invokeBeanFactoryPostProcessors(priorityOrderedPostProcessors, beanFactory);

		// Next, invoke the BeanFactoryPostProcessors that implement Ordered.
		// 创建存放实现 Ordered 接口的 BeanFactoryPostProcessor 集合
		List<BeanFactoryPostProcessor> orderedPostProcessors = new ArrayList<>(orderedPostProcessorNames.size());
		for (String postProcessorName : orderedPostProcessorNames) {
			// 实现 Ordered 接口的实例添加到集合
			orderedPostProcessors.add(beanFactory.getBean(postProcessorName, BeanFactoryPostProcessor.class));
		}
		sortPostProcessors(orderedPostProcessors, beanFactory);
		// 遍历实现了 Ordered 接口的 BeanFactoryPostProcessor，执行 postProcessBeanFactory 方法
		invokeBeanFactoryPostProcessors(orderedPostProcessors, beanFactory);

		// Finally, invoke all other BeanFactoryPostProcessors.
		// 存放普通的 BeanFactoryPostProcessors 的集合
		List<BeanFactoryPostProcessor> nonOrderedPostProcessors = new ArrayList<>(nonOrderedPostProcessorNames.size());
		for (String postProcessorName : nonOrderedPostProcessorNames) {
			nonOrderedPostProcessors.add(beanFactory.getBean(postProcessorName, BeanFactoryPostProcessor.class));
		}
		invokeBeanFactoryPostProcessors(nonOrderedPostProcessors, beanFactory);

		// Clear cached merged bean definitions since the post-processors might have
		// modified the original metadata, e.g. replacing placeholders in values...
		beanFactory.clearMetadataCache();
	}

	public static void registerBeanPostProcessors(
			ConfigurableListableBeanFactory beanFactory, AbstractApplicationContext applicationContext) {

		// 找到所有实现了 BeanPostProcessor 接口的类
		String[] postProcessorNames = beanFactory.getBeanNamesForType(BeanPostProcessor.class, true, false);

		// Register BeanPostProcessorChecker that logs an info message when
		// a bean is created during BeanPostProcessor instantiation, i.e. when
		// a bean is not eligible for getting processed by all BeanPostProcessors.
		// 记录下 BeanPostProcessor 的目标计数
		// +1 是因为会添加一个 BeanPostProcessorChecker 的类
		int beanProcessorTargetCount = beanFactory.getBeanPostProcessorCount() + 1 + postProcessorNames.length;
		// 添加 BeanPostProcessorChecker（主要用于记录信息）到 beanFactory 中
		beanFactory.addBeanPostProcessor(new BeanPostProcessorChecker(beanFactory, beanProcessorTargetCount));

		// Separate between BeanPostProcessors that implement PriorityOrdered,
		// Ordered, and the rest.
		// 定义存放实现了 PriorityOrdered 接口的 BeanPostProcessor 集合
		List<BeanPostProcessor> priorityOrderedPostProcessors = new ArrayList<>();
		// 定义存放 Spring 内部的 BeanPostProcessor
		List<BeanPostProcessor> internalPostProcessors = new ArrayList<>();
		// 定义存放实现了 Ordered 接口的 BeanPostProcessor 的 name 集合
		List<String> orderedPostProcessorNames = new ArrayList<>();
		// 存放普通的 BeanPostProcessor 的 name 集合
		List<String> nonOrderedPostProcessorNames = new ArrayList<>();
		// 遍历 beanFactory 中存在的 BeanPostProcessor 的集合 postProcessorNames
		for (String ppName : postProcessorNames) {
			// 如果 ppName 对应的 BeanPostProcessor 实例实现了PriorityOrdered 接口，获取到 ppName 对应的 BeanPostProcessor 的实例添加到 priorityOrderedPostProcessor 中
			if (beanFactory.isTypeMatch(ppName, PriorityOrdered.class)) {
				BeanPostProcessor pp = beanFactory.getBean(ppName, BeanPostProcessor.class);
				priorityOrderedPostProcessors.add(pp);
				// 如果 ppName 对应的 BeanPostProcessor实例也实现了 MergedBeanDefinitionPostProcessor接口，将 ppName 对应的 bean 实例添加到 internal... 中
				if (pp instanceof MergedBeanDefinitionPostProcessor) {
					internalPostProcessors.add(pp);
				}
			}
			// 如果 ppName 对应的 BeanPostProcessor 实例没有实现 PriorityOrdered 接口，实现了 Ordered 接口；ppName 对应的 bean 实例
			else if (beanFactory.isTypeMatch(ppName, Ordered.class)) {
				orderedPostProcessorNames.add(ppName);
			}
			else {
				// PriorityOrdered、Ordered 接口都没有实现
				nonOrderedPostProcessorNames.add(ppName);
			}
		}

		// First, register the BeanPostProcessors that implement PriorityOrdered.
		// 1. 对实现 PriorityOrdered 接口的 BeanPostProcessor 实例进行排序
		sortPostProcessors(priorityOrderedPostProcessors, beanFactory);
		// 注册实现了 PriorityOrdered 接口的 BeanPostProcessor 实例添加到 beanFactory 中
		registerBeanPostProcessors(beanFactory, priorityOrderedPostProcessors);

		// Next, register the BeanPostProcessors that implement Ordered.
		// 2. 注册所有实现 Ordered 接口的 BeanPostProcessor
		List<BeanPostProcessor> orderedPostProcessors = new ArrayList<>(orderedPostProcessorNames.size());
		for (String ppName : orderedPostProcessorNames) {
			BeanPostProcessor pp = beanFactory.getBean(ppName, BeanPostProcessor.class);
			// 将实现 Ordered 接口的 BeanPostProcessor 添加到集合中
			orderedPostProcessors.add(pp);
			// 如果 ppName 对应的 BeanPostProcessor 实例也实现了 MergedBeanDefinitionPostProcessor 接口，那么将 ppName 对应的 bean 实例添加到 internalPostProcessors 集合中
			if (pp instanceof MergedBeanDefinitionPostProcessor) {
				internalPostProcessors.add(pp);
			}
		}
		// 对实现了 Ordered 接口的 BeanPostProcessor 进行排序
		sortPostProcessors(orderedPostProcessors, beanFactory);
		// 注册实现了 Ordered 接口的 BeanPostProcessor 实例添加到 beanFactory
		registerBeanPostProcessors(beanFactory, orderedPostProcessors);

		// Now, register all regular BeanPostProcessors.
		// 存放没有实现PriorityOrdered 和 Ordered 接口的 BeanPostProcessor
		List<BeanPostProcessor> nonOrderedPostProcessors = new ArrayList<>(nonOrderedPostProcessorNames.size());
		for (String ppName : nonOrderedPostProcessorNames) {
			BeanPostProcessor pp = beanFactory.getBean(ppName, BeanPostProcessor.class);
			// 没有实现上面两个接口的 BeanPostProcessor 添加到集合中
			nonOrderedPostProcessors.add(pp);
			// ppName 实例同时也实现了 MergedBeanDefinitionPostProcessor，也添加到下面的集合中
			if (pp instanceof MergedBeanDefinitionPostProcessor) {
				internalPostProcessors.add(pp);
			}
		}
		// 注册没有实现 PriorityOrdered 和 Ordered 的 BeanPostProcessor 实例添加到 beanFactory 中
		registerBeanPostProcessors(beanFactory, nonOrderedPostProcessors);

		// Finally, re-register all internal BeanPostProcessors.
		// 将所有实现了 MergedBeanDefinitionPostProcessor 类型的 BeanPostProcessor 进行排序操作
		sortPostProcessors(internalPostProcessors, beanFactory);
		// 注册所有实现了 MergedBeanDefinitionPostProcessor 类型的 BeanPostProcessor 到 beanFactory 中
		registerBeanPostProcessors(beanFactory, internalPostProcessors);

		// Re-register post-processor for detecting inner beans as ApplicationListeners,
		// moving it to the end of the processor chain (for picking up proxies etc).
		// 注册 ApplicationListenerDetector 到 beanFactory 中（之前注册过，重新注册，将该监听器放到最后面）
		beanFactory.addBeanPostProcessor(new ApplicationListenerDetector(applicationContext));
	}

	private static void sortPostProcessors(List<?> postProcessors, ConfigurableListableBeanFactory beanFactory) {
		// Nothing to sort? 不做排序
		if (postProcessors.size() <= 1) {
			return;
		}
		Comparator<Object> comparatorToUse = null;
		if (beanFactory instanceof DefaultListableBeanFactory) {
			// 获取设置的比较器
			comparatorToUse = ((DefaultListableBeanFactory) beanFactory).getDependencyComparator();
		}
		if (comparatorToUse == null) {
			// 默认使用 OrderComparator 比较器
			comparatorToUse = OrderComparator.INSTANCE;
		}
		// 使用比较器对 postProcessors 进行排序
		postProcessors.sort(comparatorToUse);
	}

	/**
	 *
	 *
	 * Invoke the given BeanDefinitionRegistryPostProcessor beans.
	 */
	private static void invokeBeanDefinitionRegistryPostProcessors(
			Collection<? extends BeanDefinitionRegistryPostProcessor> postProcessors, BeanDefinitionRegistry registry) {
		// 遍历 currentRegistryProcessors，执行 postProcessBeanDefinitionRegistry 方法
		for (BeanDefinitionRegistryPostProcessor postProcessor : postProcessors) {
			postProcessor.postProcessBeanDefinitionRegistry(registry);
		}
	}

	/**
	 * Invoke the given BeanFactoryPostProcessor beans.
	 */
	private static void invokeBeanFactoryPostProcessors(
			Collection<? extends BeanFactoryPostProcessor> postProcessors, ConfigurableListableBeanFactory beanFactory) {

		for (BeanFactoryPostProcessor postProcessor : postProcessors) {
			postProcessor.postProcessBeanFactory(beanFactory);
		}
	}

	/**
	 * Register the given BeanPostProcessor beans.
	 */
	private static void registerBeanPostProcessors(
			ConfigurableListableBeanFactory beanFactory, List<BeanPostProcessor> postProcessors) {
		// 遍历 postProcessors；将 postProcessor 添加到 beanFactory 中的 beanPostProcessors 缓存
		for (BeanPostProcessor postProcessor : postProcessors) {
			beanFactory.addBeanPostProcessor(postProcessor);
		}
	}


	/**
	 * BeanPostProcessor that logs an info message when a bean is created during
	 * BeanPostProcessor instantiation, i.e. when a bean is not eligible for
	 * getting processed by all BeanPostProcessors.
	 */
	private static final class BeanPostProcessorChecker implements BeanPostProcessor {

		private static final Log logger = LogFactory.getLog(BeanPostProcessorChecker.class);

		private final ConfigurableListableBeanFactory beanFactory;

		private final int beanPostProcessorTargetCount;

		public BeanPostProcessorChecker(ConfigurableListableBeanFactory beanFactory, int beanPostProcessorTargetCount) {
			this.beanFactory = beanFactory;
			this.beanPostProcessorTargetCount = beanPostProcessorTargetCount;
		}

		@Override
		public Object postProcessBeforeInitialization(Object bean, String beanName) {
			return bean;
		}

		@Override
		public Object postProcessAfterInitialization(Object bean, String beanName) {
			// 1. BeanPostProcessor 类型不检测
			// 2. ROLE_INFRASTRUCTURE 这种类型的 bean 不检测（Spring 自己的 bean）
			if (!(bean instanceof BeanPostProcessor) && !isInfrastructureBean(beanName) &&
					this.beanFactory.getBeanPostProcessorCount() < this.beanPostProcessorTargetCount) {
				if (logger.isInfoEnabled()) {
					logger.info("Bean '" + beanName + "' of type [" + bean.getClass().getName() +
							"] is not eligible for getting processed by all BeanPostProcessors " +
							"(for example: not eligible for auto-proxying)");
				}
			}
			return bean;
		}

		/**
		 * 检测当前 bean 是否是 Spring 自己的 bean
		 * @param beanName
		 * @return
		 */
		private boolean isInfrastructureBean(@Nullable String beanName) {
			if (beanName != null && this.beanFactory.containsBeanDefinition(beanName)) {
				BeanDefinition bd = this.beanFactory.getBeanDefinition(beanName);
				return (bd.getRole() == RootBeanDefinition.ROLE_INFRASTRUCTURE);
			}
			return false;
		}
	}

}
