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

package org.springframework.beans.factory.support;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeansException;
import org.springframework.beans.TypeConverter;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.BeanDefinitionStoreException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.config.DependencyDescriptor;
import org.springframework.beans.factory.config.NamedBeanHolder;
import org.springframework.beans.factory.config.RuntimeBeanNameReference;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.config.TypedStringValue;
import org.springframework.lang.Nullable;
import org.springframework.util.ClassUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

/**
 * 在 bean 工厂实现中使用 Helper 类，它将 beanDefinition 对象中包含的值解析为应用于目标 bean 实例的实际值
 *
 * Helper class for use in bean factory implementations,
 * resolving values contained in bean definition objects
 * into the actual values applied to the target bean instance.
 *
 * <p>Operates on an {@link AbstractBeanFactory} and a plain
 * {@link org.springframework.beans.factory.config.BeanDefinition} object.
 * Used by {@link AbstractAutowireCapableBeanFactory}.
 *
 * @author Juergen Hoeller
 * @since 1.2
 * @see AbstractAutowireCapableBeanFactory
 */
class BeanDefinitionValueResolver {

	// 当前 bean 工厂
	private final AbstractAutowireCapableBeanFactory beanFactory;

	// 要使用的 bean 名称
	private final String beanName;

	// beanName 对应的 BeanDefinition
	private final BeanDefinition beanDefinition;

	// 用于解析TypeStringValues的TypeConverter
	private final TypeConverter typeConverter;


	/**
	 * 为给定 BeanFactory和BeanDefinition 创建一个 BeanDefinitionValueResolver 实例
	 *
	 * Create a BeanDefinitionValueResolver for the given BeanFactory and BeanDefinition.
	 * @param beanFactory the BeanFactory to resolve against
	 * @param beanName the name of the bean that we work on
	 * @param beanDefinition the BeanDefinition of the bean that we work on
	 * @param typeConverter the TypeConverter to use for resolving TypedStringValues
	 */
	public BeanDefinitionValueResolver(AbstractAutowireCapableBeanFactory beanFactory, String beanName,
			BeanDefinition beanDefinition, TypeConverter typeConverter) {

		this.beanFactory = beanFactory;
		this.beanName = beanName;
		this.beanDefinition = beanDefinition;
		this.typeConverter = typeConverter;
	}


	/**
	 * Given a PropertyValue, return a value, resolving any references to other
	 * beans in the factory if necessary. The value could be:
	 * <li>A BeanDefinition, which leads to the creation of a corresponding
	 * new bean instance. Singleton flags and names of such "inner beans"
	 * are always ignored: Inner beans are anonymous prototypes.
	 * <li>A RuntimeBeanReference, which must be resolved.
	 * <li>A ManagedList. This is a special collection that may contain
	 * RuntimeBeanReferences or Collections that will need to be resolved.
	 * <li>A ManagedSet. May also contain RuntimeBeanReferences or
	 * Collections that will need to be resolved.
	 * <li>A ManagedMap. In this case the value may be a RuntimeBeanReference
	 * or Collection that will need to be resolved.
	 * <li>An ordinary object or {@code null}, in which case it's left alone.
	 * @param argName the name of the argument that the value is defined for
	 * @param value the value object to resolve
	 * @return the resolved object
	 */
	@Nullable
	public Object resolveValueIfNecessary(Object argName, @Nullable Object value) {
		// We must check each value to see whether it requires a runtime reference
		// to another bean to be resolved.
		// 我们必需检查每个值，以查看它是否需要对另一个 bean 的运行时引用才能解决
		// RuntimeBeanReference:当属性值对象是工厂中另一个 bean 的引用时，使用不可变的占位符类，在运行时进行解析

		if (value instanceof RuntimeBeanReference) {
			RuntimeBeanReference ref = (RuntimeBeanReference) value;
			// 解析出对应 ref 所封装的 Bean 元信息(即 Bean 名,Bean 类型)的 Bean 对象:
			return resolveReference(argName, ref);
		}
		// RuntimeBeanNameReference 对应于引用类型 	<ref bean="book"></ref>
		// ref 标签注入的是目标 bean 的 id 而不是目标 bean 的实例，同时使用 ref 标签容器在部署的时候还会验证这个名称的 bean 是否真实存在。
		// 其实 id ref 就跟 value 一样，只是将某个字符串注入到属性或者构造函数中，只不过注入的是某个 Bean 定义的 id 属性值:
		// 即: <id ref bean="bea" /> 等同于 <value> bea </value>
		// 如果 values 是 RuntimeBeanReference 实例
		else if (value instanceof RuntimeBeanNameReference) {
			// 从 value 中获取引用的 bean 名
			String refName = ((RuntimeBeanNameReference) value).getBeanName();
			// 对 refName 进行解析，然后重新赋值给 refName
			refName = String.valueOf(doEvaluate(refName));
			// 如果该 bean 工厂不包含具有 refName的beanDefinition 或外部注册的 singleton 实例
			if (!this.beanFactory.containsBean(refName)) {
				throw new BeanDefinitionStoreException(
						"Invalid bean name '" + refName + "' in bean reference for " + argName);
			}
			// 返回经过解析且经过检查其是否存在于 Bean 工厂的引用 Bean 名【refName】
			return refName;
		}
		// BeanDefinitionHolder:包装类，包含 bean 名称和别名的 bean 定义的持有者，可以注册为内部 bean 的占位符
		else if (value instanceof BeanDefinitionHolder) {
			// Resolve BeanDefinitionHolder: contains BeanDefinition with name and aliases.
			BeanDefinitionHolder bdHolder = (BeanDefinitionHolder) value;
			// 根据 BeanDefinitionHolder 所封装的 Bean 名和 BeanDefinition 对象解析出内部 Bean 对象
			return resolveInnerBean(argName, bdHolder.getBeanName(), bdHolder.getBeanDefinition());
		}
		// 一般在内部匿名 bean 的配置才会出现 BeanDefinition
		else if (value instanceof BeanDefinition) {
			// Resolve plain BeanDefinition, without contained name: use dummy name.
			// 解析纯 BeanDefinition,不包含名称：使用虚拟名称
			BeanDefinition bd = (BeanDefinition) value;
			// 拼装内部Bean名:"(inner bean)#"+bd的身份哈希码的十六进制字符串形式
			String innerBeanName = "(inner bean)" + BeanFactoryUtils.GENERATED_BEAN_NAME_SEPARATOR +
					ObjectUtils.getIdentityHexString(bd);
			// 根据 innerBeanName 和 bd 解析出内部 Bean 对象
			return resolveInnerBean(argName, innerBeanName, bd);
		}
		else if (value instanceof DependencyDescriptor) {
			// 定义一个用于存放所找到的所有候选 Bean 名的集合，初始化长度为4
			Set<String> autowiredBeanNames = new LinkedHashSet<>(4);
			// 根据 descriptor 的依赖类型解析出与 descriptor 所包装的对象匹配的候选 Bean 对象
			Object result = this.beanFactory.resolveDependency(
					(DependencyDescriptor) value, this.beanName, autowiredBeanNames, this.typeConverter);
			for (String autowiredBeanName : autowiredBeanNames) {
				// 如果该 bean 工厂包含具有 autowiredBeanName 的 beanDefinition 或外部注册的 singleton 实例：
				if (this.beanFactory.containsBean(autowiredBeanName)) {
					// 注册 autowiredBeanName 与 beanName 的依赖关系
					this.beanFactory.registerDependentBean(autowiredBeanName, this.beanName);
				}
			}
			// 返回与 descriptor 所包装的对象匹配的候选 Bean 对象
			return result;
		}
		// 数组类型
		else if (value instanceof ManagedArray) {
			// May need to resolve contained runtime references.
			// 可能需要解析包含的运行时引用，将 value 强转为 ManagedArray 对象
			ManagedArray array = (ManagedArray) value;
			// 获取 array 的已解析元素类型
			Class<?> elementType = array.resolvedElementType;
			if (elementType == null) {
				// 获取 array 的元素类型名，指 array 标签的 value-type 属性
				String elementTypeName = array.getElementTypeName();
				// 如果 elementTypeName 不是空字符串
				if (StringUtils.hasText(elementTypeName)) {
					try {
						// 使用 Bean 工厂的 Bean 类型加载器加载 elementTypeName 对应的 Class 对象。
						elementType = ClassUtils.forName(elementTypeName, this.beanFactory.getBeanClassLoader());
						// 让 array#resolvedElementType 属性引用 elementType
						array.resolvedElementType = elementType;
					}
					// 捕捉加载 elementTypeName 对应的Class对象的所有异常
					catch (Throwable ex) {
						// Improve the message by showing the context.
						throw new BeanCreationException(
								this.beanDefinition.getResourceDescription(), this.beanName,
								"Error resolving array type for " + argName, ex);
					}
				}
				else {
					// elementType 默认使用 Object 类对象
					elementType = Object.class;
				}
			}
			// 解析 ManagedArra y对象，以得到解析后的数组对象
			return resolveManagedArray(argName, (List<?>) value, elementType);
		}
		// List 类型
		else if (value instanceof ManagedList) {
			// May need to resolve contained runtime references.
			// 可能需要解析包含的运行时引用，解析 ManagedList 对象，以得到解析后的 List 对象并结果返回出去
			return resolveManagedList(argName, (List<?>) value);
		}
		// Set 类型
		else if (value instanceof ManagedSet) {
			// May need to resolve contained runtime references.
			// 可能需要解析包含的运行时引用，解析 ManagedSet 对象，以得到解析后的 Set 对象并结果返回出去
			return resolveManagedSet(argName, (Set<?>) value);
		}
		// Map 类型
		else if (value instanceof ManagedMap) {
			// May need to resolve contained runtime references.
			// 可能需要解析包含的运行时引用，解析 ManagedMap 对象，以得到解析后的 Map 对象并结果返回出去
			return resolveManagedMap(argName, (Map<?, ?>) value);
		}
		// Properties 类型
		else if (value instanceof ManagedProperties) {
			Properties original = (Properties) value;
			// 定义一个用于存储将 original 的所有 Property 的键/值解析后的键/值的 Properties 对象
			Properties copy = new Properties();
			// 遍历 original，键名为 propKey,值为 propValue
			original.forEach((propKey, propValue) -> {
				// 如果 proKey 是 TypeStringValue 实例
				if (propKey instanceof TypedStringValue) {
					// 在 propKey 封装的 value 可解析成表达式的情况下,将 propKey 封装的 value 评估为表达式并解析出表达式的值
					propKey = evaluate((TypedStringValue) propKey);
				}
				if (propValue instanceof TypedStringValue) {
					// 在 propValue 封装的 value 可解析成表达式的情况下,将 propValue 封装的 value 评估为表达式并解析出表达式的值
					propValue = evaluate((TypedStringValue) propValue);
				}
				// 如果 proKey 或者 propValue 为null
				if (propKey == null || propValue == null) {
					// 抛出 Bean 创建异常:转换 argName 的属性键/值时出错：解析为 null
					throw new BeanCreationException(
							this.beanDefinition.getResourceDescription(), this.beanName,
							"Error converting Properties key/value pair for " + argName + ": resolved to null");
				}
				// 将 propKey 和 propValue 添加到 copy 中
				copy.put(propKey, propValue);
			});
			return copy;
		}
		// String 类型
		else if (value instanceof TypedStringValue) {
			// Convert value to target type here.
			TypedStringValue typedStringValue = (TypedStringValue) value;
			//在 typedStringValue 封装的 value 可解析成表达式的情况下,将 typedStringValue 封装的 value 评估为表达式并解析出表达式的值
			Object valueObject = evaluate(typedStringValue);
			try {
				// 在 typedStringValue 中解析目标类型
				Class<?> resolvedTargetType = resolveTargetType(typedStringValue);
				if (resolvedTargetType != null) {
					// 使用 typeConverter 将值转换为所需的类型
					return this.typeConverter.convertIfNecessary(valueObject, resolvedTargetType);
				}
				else {
					// 返回并解析出来表达式的值
					return valueObject;
				}
			}
			// 捕捉在解析目标类型或转换类型过程中抛出的异常
			catch (Throwable ex) {
				// Improve the message by showing the context.
				throw new BeanCreationException(
						this.beanDefinition.getResourceDescription(), this.beanName,
						"Error converting typed String value for " + argName, ex);
			}
		}
		else if (value instanceof NullBean) {
			return null;
		}
		else {
			// 对于 value 是 String/String[] 类型会尝试评估为表达式并解析出表达式的值，其他类型直接返回value.
			return evaluate(value);
		}
	}

	/**
	 * 在value封装的value可解析成表达式的情况下,将value封装的value评估为表达式并解析出表达式的值
	 *
	 * Evaluate the given value as an expression, if necessary.
	 * @param value the candidate value (may be an expression)
	 * @return the resolved value
	 */
	@Nullable
	protected Object evaluate(TypedStringValue value) {
		// 如有必要(value可解析成表达式的情况下)，将 value 封装的 value 评估为表达式并解析出表达式的值
		Object result = doEvaluate(value.getValue());
		// 如果 result 与 value 所封装的 value 不相等
		if (!ObjectUtils.nullSafeEquals(result, value.getValue())) {
			// 将 value 标记为动态，即包含一个表达式，因此不进行缓存
			value.setDynamic();
		}
		return result;
	}

	/**
	 * 对于value是String/String[]类型会尝试评估为表达式并解析出表达式的值，其他类型直接返回value
	 *
	 * Evaluate the given value as an expression, if necessary.
	 * @param value the original value (may be an expression)
	 * @return the resolved value if necessary, or the original value
	 */
	@Nullable
	protected Object evaluate(@Nullable Object value) {
		if (value instanceof String) {
			// 如有必要(value可解析成表达式的情况下)，将value评估为表达式并解析出表达式的值并返回出去
			return doEvaluate((String) value);
		}
		else if (value instanceof String[]) {
			String[] values = (String[]) value;
			// 是否经过解析的标记，默认为 false
			boolean actuallyResolved = false;
			// 定义用于存放解析的值的 Object 数组，长度为 values 的长度
			Object[] resolvedValues = new Object[values.length];
			for (int i = 0; i < values.length; i++) {
				String originalValue = values[i];
				// 如有必要(value可解析成表达式的情况下)，将originalValue评估为表达式并解析出表达式的值
				Object resolvedValue = doEvaluate(originalValue);
				// 如果 resolvedValue 与 originalValue 不是同一个对象
				if (resolvedValue != originalValue) {
					// 经过解析标记为true，表示已经过解析
					actuallyResolved = true;
				}
				// 将resolvedValue赋值第 i 个 resolvedValues 元素中
				resolvedValues[i] = resolvedValue;
			}
			// 如果已经过解析，返回解析后的数组【resolvedValues】；否则返回values
			return (actuallyResolved ? resolvedValues : values);
		}
		else {
			// 其他类型直接返回value
			return value;
		}
	}

	/**
	 * 如有必要(value可解析成表达式的情况下)，将给定的String值评估为表达式并解析出表达式的值
	 *
	 * Evaluate the given String value as an expression, if necessary.
	 * @param value the original value (may be an expression)
	 * @return the resolved value if necessary, or the original String value
	 */
	@Nullable
	private Object doEvaluate(@Nullable String value) {
		// 评估value,如果value是可解析表达式，会对其进行解析，否则直接返回value
		return this.beanFactory.evaluateBeanDefinitionString(value, this.beanDefinition);
	}

	/**
	 * 在给定的TypedStringValue中解析目标类型
	 *
	 * Resolve the target type in the given TypedStringValue.
	 * @param value the TypedStringValue to resolve
	 * @return the resolved target type (or {@code null} if none specified)
	 * @throws ClassNotFoundException if the specified type cannot be resolved
	 * @see TypedStringValue#resolveTargetType
	 */
	@Nullable
	protected Class<?> resolveTargetType(TypedStringValue value) throws ClassNotFoundException {
		// 如果 value 有携带目标类型
		if (value.hasTargetType()) {
			// 返回 value 的目标类型
			return value.getTargetType();
		}
		// 从 value 中解析出目标类型
		return value.resolveTargetType(this.beanFactory.getBeanClassLoader());
	}

	/**
	 * 解析出对应ref所封装的Bean元信息(即Bean名,Bean类型)的Bean对象，在工厂中解决对另一个bean的引用
	 *
	 * Resolve a reference to another bean in the factory.
	 */
	@Nullable
	private Object resolveReference(Object argName, RuntimeBeanReference ref) {
		try {
			Object bean;
			// 获取另一个 Bean 引用的 Bean 类型
			Class<?> beanType = ref.getBeanType();
			// 如果引用来自父工厂
			if (ref.isToParent()) {
				BeanFactory parent = this.beanFactory.getParentBeanFactory();
				if (parent == null) {
					// 没有父工厂，抛出 Bean 创建异常：无法解析对 bean 的引用 ref 在父工厂中:没有可以的父工厂
					throw new BeanCreationException(
							this.beanDefinition.getResourceDescription(), this.beanName,
							"Cannot resolve reference to bean " + ref +
									" in parent factory: no parent factory available");
				}
				if (beanType != null) {
					// 从父工厂中获取引用的 Bean 类型对应的 Bean 对象
					bean = parent.getBean(beanType);
				}
				else {
					// 否则,使用引用的 Bean 名,从父工厂中获取对应的 Bean 对像
					bean = parent.getBean(String.valueOf(doEvaluate(ref.getBeanName())));
				}
			}
			else {
				// 定义一个用于存储解析出来的 Bean 名的变量
				String resolvedName;
				if (beanType != null) {
					// 解析与 beanType 唯一匹配的 bean 实例，包括其 bean 名
					NamedBeanHolder<?> namedBean = this.beanFactory.resolveNamedBean(beanType);
					// 让 bean 引用 nameBean 所封装的 Bean 对象
					bean = namedBean.getBeanInstance();
					// 让 resolvedName 引用 nameBean 所封装的 Bean 名
					resolvedName = namedBean.getBeanName();
				}
				else {
					// 让 resolvedName 引用 ref 所包装的 Bean 名
					resolvedName = String.valueOf(doEvaluate(ref.getBeanName()));
					// 获取 resolvedName的Bean 对象
					bean = this.beanFactory.getBean(resolvedName);
				}
				// 注册 beanName 与 dependentBeanNamed 的依赖关系到 Bean 工厂
				this.beanFactory.registerDependentBean(resolvedName, this.beanName);
			}
			if (bean instanceof NullBean) {
				bean = null;
			}
			// 返回解析出来对应 ref 所封装的 Bean 元信息(即 Bean 名,Bean 类型)的 Bean 对象
			return bean;
		}
		// 捕捉Bean包和子包中引发的所有异常
		catch (BeansException ex) {
			throw new BeanCreationException(
					this.beanDefinition.getResourceDescription(), this.beanName,
					"Cannot resolve reference to bean '" + ref.getBeanName() + "' while setting " + argName, ex);
		}
	}

	/**
	 * 解析出内部 Bean 对象
	 *
	 * Resolve an inner bean definition.
	 * @param argName the name of the argument that the inner bean is defined for
	 * @param innerBeanName the name of the inner bean
	 * @param innerBd the bean definition for the inner bean
	 * @return the resolved inner bean instance
	 */
	@Nullable
	private Object resolveInnerBean(Object argName, String innerBeanName, BeanDefinition innerBd) {
		// 定义一个用于保存 innerBd 与 beanDefinition 合并后的 BeanDefinition 对象的变量
		RootBeanDefinition mbd = null;
		try {
			// 获取 innerBd 与 beanDefinition 合并后的 BeanDefinition 对象
			mbd = this.beanFactory.getMergedBeanDefinition(innerBeanName, innerBd, this.beanDefinition);
			// Check given bean name whether it is unique. If not already unique,
			// add counter - increasing the counter until the name is unique.
			// 检查给定的 Bean 名是否唯一。如果还不是唯一的,添加计数器-增加计数器,直到名称唯一为止.
			// 解决内部 Bean 名需要唯一的问题
			// 定义实际的内部 Bean 名,初始为 innerBeanName
			String actualInnerBeanName = innerBeanName;
			// 如果 mbd 配置成了单例
			if (mbd.isSingleton()) {
				// 调整 innerBeanName,直到该 Bean 名在工厂中唯一。最后将结果赋值给 actualInnerBeanName
				actualInnerBeanName = adaptInnerBeanName(innerBeanName);
			}
			// 将 actualInnerBeanName 和 beanName 的包含关系注册到该工厂中
			this.beanFactory.registerContainedBean(actualInnerBeanName, this.beanName);
			// Guarantee initialization of beans that the inner bean depends on.
			// 确保内部 Bean 依赖的 Bean 的初始化，获取 mdb 的要依赖的 Bean 名
			String[] dependsOn = mbd.getDependsOn();
			// 如果有需要依赖的 Bean 名
			if (dependsOn != null) {
				for (String dependsOnBean : dependsOn) {
					// 注册 dependsOnBean 与 actualInnerBeanName 的依赖关系到该工厂中
					this.beanFactory.registerDependentBean(dependsOnBean, actualInnerBeanName);
					// 获取 dependsOnBean的Bean 对像(不引用，只是为了让 dependsOnBean 所对应的 Bean 对象实例化)
					this.beanFactory.getBean(dependsOnBean);
				}
			}
			// Actually create the inner bean instance now...
			// 实际上现有创建内部 bean 实例，创建 actualInnerBeanName的Bean 对象
			Object innerBean = this.beanFactory.createBean(actualInnerBeanName, mbd, null);
			// 如果 innerBean 时 FactoryBean 的实例
			if (innerBean instanceof FactoryBean) {
				// mbd 是否是 "synthetic" 的标记。一般是指只有AOP相关的 pointCut 配置或者 Advice 配置才会将 synthetic 设置为 true
				boolean synthetic = mbd.isSynthetic();
				// 从 BeanFactory 对象中获取管理的对象，只有 mbd 不是 synthetic 才对其对象进行该工厂的后置处理
				innerBean = this.beanFactory.getObjectFromFactoryBean(
						(FactoryBean<?>) innerBean, actualInnerBeanName, !synthetic);
			}
			if (innerBean instanceof NullBean) {
				innerBean = null;
			}
			// 返回 actualInnerBeanName 的Bean对象【innerBean】
			return innerBean;
		}
		// 捕捉解析内部Bean对象过程中抛出的Bean包和子包中引发的所有异常
		catch (BeansException ex) {
			throw new BeanCreationException(
					this.beanDefinition.getResourceDescription(), this.beanName,
					"Cannot create inner bean '" + innerBeanName + "' " +
					(mbd != null && mbd.getBeanClassName() != null ? "of type [" + mbd.getBeanClassName() + "] " : "") +
					"while setting " + argName, ex);
		}
	}

	/**
	 * 检查给定Bean名是否唯一.如果还不是唯一的,则添加该计数器,直到名称唯一位置
	 *
	 * Checks the given bean name whether it is unique. If not already unique,
	 * a counter is added, increasing the counter until the name is unique.
	 * @param innerBeanName the original name for the inner bean
	 * @return the adapted name for the inner bean
	 */
	private String adaptInnerBeanName(String innerBeanName) {
		// 定义一个实际内部 Bean 名变量，初始为 innerBean 名
		String actualInnerBeanName = innerBeanName;
		// 定义一个用于计数的计数器，初始为 0
		int counter = 0;
		// 获取前缀
		String prefix = innerBeanName + BeanFactoryUtils.GENERATED_BEAN_NAME_SEPARATOR;
		// 只要 actualInnerBeanName 是否已在该工厂中使用就继续循环,即 actualInnerBeanName 是否是别名
		// 或该工厂是否已包含 actualInnerBeanName的bean 对象 或 该工厂是否已经为 actualInnerBeanName 注册了依赖 Bean 关系
		while (this.beanFactory.isBeanNameInUse(actualInnerBeanName)) {
			counter++;
			// 让 actualInnerBeanName 重新引用拼接后的字符串:innerBeanName+'#'+count
			actualInnerBeanName = prefix + counter;
		}
		// 返回经过调整后的 Bean 名
		return actualInnerBeanName;
	}

	/**
	 * 解析ManagedArray对象，以得到解析后的数组对象
	 *
	 * For each element in the managed array, resolve reference if necessary.
	 */
	private Object resolveManagedArray(Object argName, List<?> ml, Class<?> elementType) {
		// 创建一个用于存放解析后的实例对象的 elementType 类型长度为 ml 大小的数组
		Object resolved = Array.newInstance(elementType, ml.size());
		for (int i = 0; i < ml.size(); i++) {
			// 获取第i个 ml 元素对象，解析出该元素对象的实例对象然后设置到第 i 个 resolved 元素中
			Array.set(resolved, i, resolveValueIfNecessary(new KeyedArgName(argName, i), ml.get(i)));
		}
		// 返回解析后的的数组对象【resolved】
		return resolved;
	}

	/**
	 * 解析ManagedList对象，以得到解析后的List对象
	 *
	 * For each element in the managed list, resolve reference if necessary.
	 */
	private List<?> resolveManagedList(Object argName, List<?> ml) {
		List<Object> resolved = new ArrayList<>(ml.size());
		for (int i = 0; i < ml.size(); i++) {
			// 获取第i个 ml 元素对象，解析出该元素对象的实例对象然后添加到 resolved 中
			resolved.add(resolveValueIfNecessary(new KeyedArgName(argName, i), ml.get(i)));
		}
		return resolved;
	}

	/**
	 * 解析ManagedSet对象，以得到解析后的Set对象
	 *
	 * For each element in the managed set, resolve reference if necessary.
	 */
	private Set<?> resolveManagedSet(Object argName, Set<?> ms) {
		Set<Object> resolved = new LinkedHashSet<>(ms.size());
		int i = 0;
		for (Object m : ms) {
			// 解析出该 m 的实例对象然后添加到 resolved 中
			resolved.add(resolveValueIfNecessary(new KeyedArgName(argName, i), m));
			i++;
		}
		return resolved;
	}

	/**
	 * 解析ManagedMap对象，以得到解析后的Map对象
	 *
	 * For each element in the managed map, resolve reference if necessary.
	 */
	private Map<?, ?> resolveManagedMap(Object argName, Map<?, ?> mm) {
		Map<Object, Object> resolved = new LinkedHashMap<>(mm.size());
		mm.forEach((key, value) -> {
			// 解析 mm 的 key 的实例对象
			Object resolvedKey = resolveValueIfNecessary(argName, key);
			// 解析 mm 的 value 的实例对象
			Object resolvedValue = resolveValueIfNecessary(new KeyedArgName(argName, key), value);
			// 将解析出来的key和value的实例对象添加到resolved中
			resolved.put(resolvedKey, resolvedValue);
		});
		return resolved;
	}


	/**
	 * 用于延迟toString构建的Holder类
	 *
	 * Holder class used for delayed toString building.
	 */
	private static class KeyedArgName {

		private final Object argName;

		private final Object key;

		public KeyedArgName(Object argName, Object key) {
			this.argName = argName;
			this.key = key;
		}

		@Override
		public String toString() {
			return this.argName + " with key " + BeanWrapper.PROPERTY_KEY_PREFIX +
					this.key + BeanWrapper.PROPERTY_KEY_SUFFIX;
		}
	}

}
