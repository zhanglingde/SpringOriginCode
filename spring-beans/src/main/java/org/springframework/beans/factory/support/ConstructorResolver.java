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

import java.beans.ConstructorProperties;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Executable;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;

import org.springframework.beans.BeanMetadataElement;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.BeansException;
import org.springframework.beans.TypeConverter;
import org.springframework.beans.TypeMismatchException;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.BeanDefinitionStoreException;
import org.springframework.beans.factory.InjectionPoint;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.NoUniqueBeanDefinitionException;
import org.springframework.beans.factory.UnsatisfiedDependencyException;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.beans.factory.config.ConstructorArgumentValues;
import org.springframework.beans.factory.config.ConstructorArgumentValues.ValueHolder;
import org.springframework.beans.factory.config.DependencyDescriptor;
import org.springframework.core.CollectionFactory;
import org.springframework.core.MethodParameter;
import org.springframework.core.NamedThreadLocal;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.MethodInvoker;
import org.springframework.util.ObjectUtils;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;

/**
 * 代表解析构造函数和工厂方法
 *
 * Delegate for resolving constructors and factory methods.
 * <p>Performs constructor resolution through argument matching.
 *
 * @author Juergen Hoeller
 * @author Rob Harrop
 * @author Mark Fisher
 * @author Costin Leau
 * @author Sebastien Deleuze
 * @author Sam Brannen
 * @since 2.0
 * @see #autowireConstructor
 * @see #instantiateUsingFactoryMethod
 * @see AbstractAutowireCapableBeanFactory
 */
class ConstructorResolver {

	private static final Object[] EMPTY_ARGS = new Object[0];

	/**
	 * 缓存的参数数组中自动装配的参数标记，以后将由解析的自动装配参数替换
	 *
	 * Marker for autowired arguments in a cached argument array, to be later replaced
	 * by a {@linkplain #resolveAutowiredArgument resolved autowired argument}.
	 */
	private static final Object autowiredArgumentMarker = new Object();

	private static final NamedThreadLocal<InjectionPoint> currentInjectionPoint =
			new NamedThreadLocal<>("Current injection point");


	private final AbstractAutowireCapableBeanFactory beanFactory;

	private final Log logger;


	/**
	 * Create a new ConstructorResolver for the given factory and instantiation strategy.
	 * @param beanFactory the BeanFactory to work with
	 */
	public ConstructorResolver(AbstractAutowireCapableBeanFactory beanFactory) {
		this.beanFactory = beanFactory;
		this.logger = beanFactory.getLogger();
	}


	/**
	 * "autowire constructor" (with constructor arguments by type) behavior.
	 * Also applied if explicit constructor argument values are specified,
	 * matching all remaining arguments with beans from the bean factory.
	 * <p>This corresponds to constructor injection: In this mode, a Spring
	 * bean factory is able to host components that expect constructor-based
	 * dependency resolution.
	 * @param beanName the name of the bean
	 * @param mbd the merged bean definition for the bean
	 * @param chosenCtors chosen candidate constructors (or {@code null} if none)
	 * @param explicitArgs argument values passed in programmatically via the getBean method,
	 * or {@code null} if none (-> use constructor argument values from bean definition)
	 * @return a BeanWrapper for the new instance
	 */
	public BeanWrapper autowireConstructor(String beanName, RootBeanDefinition mbd,
			@Nullable Constructor<?>[] chosenCtors, @Nullable Object[] explicitArgs) {

		// 实例化 BeanWrapperImpl。是包装 bean 的容器
		BeanWrapperImpl bw = new BeanWrapperImpl();
		// 给包装对象设置一些属性
		this.beanFactory.initBeanWrapper(bw);

		// spring 对这个 bean 进行实例化使用的构造函数
		Constructor<?> constructorToUse = null;
		// spring 执行构造函数使用的是参数封装类
		ArgumentsHolder argsHolderToUse = null;
		// 参与构造函数实例化过程的参数
		Object[] argsToUse = null;

		// 如果有传入参数，就直接使用传入的参数
		if (explicitArgs != null) {
			// 参数引用 explicitArgs
			argsToUse = explicitArgs;
		}
		// 没有传入参数
		else {
			// 声明一个要解析的参数值数组，默认为 null
			Object[] argsToResolve = null;
			synchronized (mbd.constructorArgumentLock) {
				// 获取 BeanDefinition 中解析完成的构造函数
				constructorToUse = (Constructor<?>) mbd.resolvedConstructorOrFactoryMethod;
				// BeanDefinition 中存在构造函数并且存在构造函数的参数，赋值进行使用
				if (constructorToUse != null && mbd.constructorArgumentsResolved) {
					// Found a cached constructor...
					// 缓存中找到了构造器，那么继续从缓存中寻找缓存的构造器参数
					argsToUse = mbd.resolvedConstructorArguments;
					if (argsToUse == null) {
						// 没有缓存的参数，就需要获取配置文件中配置的参数
						argsToResolve = mbd.preparedConstructorArguments;
					}
				}
			}
			// 如果缓存中没有缓存的参数的话，即 argsToResolve 不为空，就需要解析配置的参数
			if (argsToResolve != null) {
				// 解析参数类型，比如将配置的 String 类型转换为 list、boolean 等类型
				argsToUse = resolvePreparedArguments(beanName, mbd, bw, constructorToUse, argsToResolve, true);
			}
		}

		// 如果 constructorToUse 为 null 或者 argsToUser 为 null
		if (constructorToUse == null || argsToUse == null) {
			// Take specified constructors, if any.
			// 如果传入的构造器数组不为空，就使用传入的过后早期参数，否则通过反射获取 class 中定义的构造器
			Constructor<?>[] candidates = chosenCtors;
			if (candidates == null) {
				// 获取 mbd 的Bean类
				Class<?> beanClass = mbd.getBeanClass();
				try {
					// 使用 public 的构造器或者所有构造器
					candidates = (mbd.isNonPublicAccessAllowed() ?
							beanClass.getDeclaredConstructors() : beanClass.getConstructors());
				}
				// 捕捉获取beanClass的构造函数发出的异常
				catch (Throwable ex) {
					throw new BeanCreationException(mbd.getResourceDescription(), beanName,
							"Resolution of declared constructors on bean Class [" + beanClass.getName() +
							"] from ClassLoader [" + beanClass.getClassLoader() + "] failed", ex);
				}
			}

			// 如果candidateList只有一个元素 且 没有传入构造函数值 且 mbd也没有构造函数参数值
			if (candidates.length == 1 && explicitArgs == null && !mbd.hasConstructorArgumentValues()) {
				// 获取candidates中唯一的方法
				Constructor<?> uniqueCandidate = candidates[0];
				// 如果uniqueCandidate不需要参数
				if (uniqueCandidate.getParameterCount() == 0) {
					// 使用mdb的构造函数字段的通用锁【{@link RootBeanDefinition#constructorArgumentLock}】进行加锁以保证线程安全
					synchronized (mbd.constructorArgumentLock) {
						// 让 mbd 缓存已解析的构造函数或工厂方法
						mbd.resolvedConstructorOrFactoryMethod = uniqueCandidate;
						// 让 mbd 标记构造函数参数已解析
						mbd.constructorArgumentsResolved = true;
						// 让 mbd 缓存完全解析的构造函数参数
						mbd.resolvedConstructorArguments = EMPTY_ARGS;
					}
					// 使用constructorToUse生成与beanName对应的Bean对象,并将该Bean对象保存到bw中
					bw.setBeanInstance(instantiate(beanName, mbd, uniqueCandidate, EMPTY_ARGS));
					return bw;
				}
			}

			// Need to resolve the constructor.
			// 自动装配标识，以下有一种情况成立则为true，
			// 1、传进来构造函数，证明spring根据之前代码的判断，知道应该用哪个构造函数，
			// 2、BeanDefinition中设置为构造函数注入模型
			boolean autowiring = (chosenCtors != null ||
					mbd.getResolvedAutowireMode() == AutowireCapableBeanFactory.AUTOWIRE_CONSTRUCTOR);
			// 定义一个用于存放解析后的构造函数参数值的ConstructorArgumentValues对象
			ConstructorArgumentValues resolvedValues = null;

			// 构造函数的最小参数个数
			int minNrOfArgs;
			// 如果传入了参与构造函数实例化的参数值，那么参数的数量即为最小参数个数
			if (explicitArgs != null) {
				// minNrOfArgs引用explitArgs的数组长度
				minNrOfArgs = explicitArgs.length;
			}
			else {
				// 提取配置文件中的配置的构造函数参数
				ConstructorArgumentValues cargs = mbd.getConstructorArgumentValues();
				// 用于承载解析后的构造函数参数的值
				resolvedValues = new ConstructorArgumentValues();
				// 能解析到的参数个数
				minNrOfArgs = resolveConstructorArguments(beanName, mbd, bw, cargs, resolvedValues);
			}

			// 对候选的构造函数进行排序，先是访问权限后是参数个数
			// public 权限参数数量由多到少
			AutowireUtils.sortConstructors(candidates);
			// 定义一个差异变量，变量的大小决定着构造函数是否能够被使用
			int minTypeDiffWeight = Integer.MAX_VALUE;
			// 不明确的构造函数集合，正常情况下差异值不可能相同
			Set<Constructor<?>> ambiguousConstructors = null;
			// 定义一个用于UnsatisfiedDependencyException的列表
			LinkedList<UnsatisfiedDependencyException> causes = null;

			// 循环候选的构造函数
			for (Constructor<?> candidate : candidates) {
				// 获取参数的个数
				int parameterCount = candidate.getParameterCount();

				// 如果已经找到选用的构造函数或者需要的参数个数小于当前的构造函数参数个数则终止，前面已经经过了排序操作
				if (constructorToUse != null && argsToUse != null && argsToUse.length > parameterCount) {
					// Already found greedy constructor that can be satisfied ->
					// do not look any further, there are only less greedy constructors left.
					break;
				}
				// 如果本构造函数的参数列表数量小于要求的最小数量，则遍历下一个
				if (parameterCount < minNrOfArgs) {
					// 参数个数不相等
					continue;
				}

				// 存放构造函数解析完成的参数列表
				ArgumentsHolder argsHolder;
				// 获取参数列表的类型
				Class<?>[] paramTypes = candidate.getParameterTypes();
				// 存在需要解析的构造函数参数
				if (resolvedValues != null) {
					try {
						// 获取构造函数上的ConstructorProperties注解中的参数
						String[] paramNames = ConstructorPropertiesChecker.evaluate(candidate, parameterCount);
						// 如果没有上面的注解，则获取构造函数参数列表中属性的名称
						if (paramNames == null) {
							// 获取参数名称探索器
							ParameterNameDiscoverer pnd = this.beanFactory.getParameterNameDiscoverer();
							if (pnd != null) {
								// 获取指定构造函数的参数名称
								paramNames = pnd.getParameterNames(candidate);
							}
						}
						// 根据名称和数据类型创建参数持有者
						argsHolder = createArgumentArray(beanName, mbd, resolvedValues, bw, paramTypes, paramNames,
								getUserDeclaredConstructor(candidate), autowiring, candidates.length == 1);
					}
					catch (UnsatisfiedDependencyException ex) {
						if (logger.isTraceEnabled()) {
							logger.trace("Ignoring constructor [" + candidate + "] of bean '" + beanName + "': " + ex);
						}
						// Swallow and try next constructor.
						// 吞下并尝试下一个重载的构造函数
						// 如果 cause 为 null
						if (causes == null) {
							// 对 cause 进行实例化成 LinkedList 对象
							causes = new LinkedList<>();
						}
						//将 ex 添加到 causes 中
						causes.add(ex);
						continue;
					}
				}
				// 不存在构造函数参数列表需要解析的参数
				else {
					// Explicit arguments given -> arguments length must match exactly.
					// 如果参数列表的数量与传入进来的参数数量不相等，继续遍历，否则构造参数列表封装对象
					if (parameterCount != explicitArgs.length) {
						continue;
					}
					// 构造函数没有参数的情况
					argsHolder = new ArgumentsHolder(explicitArgs);
				}

				// 计算差异量，根据要参与构造函数的参数列表和本构造函数的参数列表进行计算
				int typeDiffWeight = (mbd.isLenientConstructorResolution() ?
						argsHolder.getTypeDifferenceWeight(paramTypes) : argsHolder.getAssignabilityWeight(paramTypes));
				// Choose this constructor if it represents the closest match.
				// 本次的构造函数差异值小于上一个构造函数，则进行构造函数更换
				if (typeDiffWeight < minTypeDiffWeight) {
					// 将确定使用的构造函数设置为本构造
					constructorToUse = candidate;
					// 更换使用的构造函数参数封装类
					argsHolderToUse = argsHolder;
					// 更换参与构造函数实例化的参数
					argsToUse = argsHolder.arguments;
					// 差异值更换
					minTypeDiffWeight = typeDiffWeight;
					// 不明确的构造函数列表清空为null
					ambiguousConstructors = null;
				}
				// 差异值相等，则表明构造函数不正常，放入异常集合
				else if (constructorToUse != null && typeDiffWeight == minTypeDiffWeight) {
					// 如果ambiguousFactoryMethods为null
					if (ambiguousConstructors == null) {
						// 初始化ambiguousFactoryMethods为LinkedHashSet实例
						ambiguousConstructors = new LinkedHashSet<>();
						// 将constructorToUse添加到ambiguousFactoryMethods中
						ambiguousConstructors.add(constructorToUse);
					}
					// 将candidate添加到ambiguousFactoryMethods中
					ambiguousConstructors.add(candidate);
				}
			}

			// 以下两种情况会抛异常
			// 1、没有确定使用的构造函数
			// 2、存在模糊的构造函数并且不允许存在模糊的构造函数
			if (constructorToUse == null) {
				if (causes != null) {
					UnsatisfiedDependencyException ex = causes.removeLast();
					for (Exception cause : causes) {
						this.beanFactory.onSuppressedException(cause);
					}
					throw ex;
				}
				throw new BeanCreationException(mbd.getResourceDescription(), beanName,
						"Could not resolve matching constructor " +
						"(hint: specify index/type/name arguments for simple parameters to avoid type ambiguities)");
			}
			else if (ambiguousConstructors != null && !mbd.isLenientConstructorResolution()) {
				throw new BeanCreationException(mbd.getResourceDescription(), beanName,
						"Ambiguous constructor matches found in bean '" + beanName + "' " +
						"(hint: specify index/type/name arguments for simple parameters to avoid type ambiguities): " +
						ambiguousConstructors);
			}

			/**
			 * 没有传入参与构造函数参数列表的参数时，对构造函数缓存到BeanDefinition中
			 * 	1、缓存BeanDefinition进行实例化时使用的构造函数
			 * 	2、缓存BeanDefinition代表的Bean的构造函数已解析完标识
			 * 	3、缓存参与构造函数参数列表值的参数列表
			 */
			if (explicitArgs == null && argsHolderToUse != null) {
				// 将解析的构造函数加入缓存
				argsHolderToUse.storeCache(mbd, constructorToUse);
			}
		}

		Assert.state(argsToUse != null, "Unresolved constructor arguments");
		// 将构造的实例加入 BeanWrapper 中
		bw.setBeanInstance(instantiate(beanName, mbd, constructorToUse, argsToUse));
		return bw;
	}

	private Object instantiate(
			String beanName, RootBeanDefinition mbd, Constructor<?> constructorToUse, Object[] argsToUse) {

		try {
			InstantiationStrategy strategy = this.beanFactory.getInstantiationStrategy();
			if (System.getSecurityManager() != null) {
				return AccessController.doPrivileged((PrivilegedAction<Object>) () ->
						strategy.instantiate(mbd, beanName, this.beanFactory, constructorToUse, argsToUse),
						this.beanFactory.getAccessControlContext());
			}
			else {
				return strategy.instantiate(mbd, beanName, this.beanFactory, constructorToUse, argsToUse);
			}
		}
		catch (Throwable ex) {
			throw new BeanCreationException(mbd.getResourceDescription(), beanName,
					"Bean instantiation via constructor failed", ex);
		}
	}

	/**
	 * 如果可能，解析指定的beanDefinition中factory方法
	 *
	 * Resolve the factory method in the specified bean definition, if possible.
	 * {@link RootBeanDefinition#getResolvedFactoryMethod()} can be checked for the result.
	 * @param mbd the bean definition to check
	 */
	public void resolveFactoryMethodIfPossible(RootBeanDefinition mbd) {
		// 定义用于引用工厂类对象的类对象
		Class<?> factoryClass;
		// 定义是否是静态标记
		boolean isStatic;
		// 如果mbd的FactoryBean名不为null
		if (mbd.getFactoryBeanName() != null) {
			// 使用beanFactory确定mbd的FactoryBean名的bean类型。为了确定其对象类型，默认让FactoryBean以初始化
			factoryClass = this.beanFactory.getType(mbd.getFactoryBeanName());
			// 静态标记设置为false，表示不是静态方法
			isStatic = false;
		}
		else {
			// 获取mbd包装好的Bean类
			factoryClass = mbd.getBeanClass();
			// 静态标记设置为true，表示是静态方法
			isStatic = true;
		}
		// 如果factoryClass为null,抛出异常：无法解析工厂类
		Assert.state(factoryClass != null, "Unresolvable factory class");
		// 如果clazz是CGLIB生成的子类，则返回该子类的父类，否则直接返回要检查的类
		factoryClass = ClassUtils.getUserClass(factoryClass);

		// 根据 mbd 的是否允许访问非公共构造函数和方法标记【RootBeanDefinition.isNonPublicAccessAllowed】来获取 factoryClass 的所有候选方法
		Method[] candidates = getCandidateMethods(factoryClass, mbd);
		// 定义用于存储唯一方法对象的 Method 对象
		Method uniqueCandidate = null;
		// 遍历 candidates
		for (Method candidate : candidates) {
			// 如果 candidate 的静态标记与静态标记相同 且 candidate 有资格作为工厂方法
			if (Modifier.isStatic(candidate.getModifiers()) == isStatic && mbd.isFactoryMethod(candidate)) {
				if (uniqueCandidate == null) {
					// uniqueCandidate 引用 candidate
					uniqueCandidate = candidate;
				}
				// 如果 uniqueCandidate 的参数类型数组与 candidate 的参数类型数组不一致
				else if (isParamMismatch(uniqueCandidate, candidate)) {
					// 取消 uniqueCandidate 的引用
					uniqueCandidate = null;
					// 跳出循环
					break;
				}
			}
		}
		// 将 mbd 用于自省的唯一工厂方法候选的缓存引用上 uniqueCandidate
		mbd.factoryMethodToIntrospect = uniqueCandidate;
	}

	private boolean isParamMismatch(Method uniqueCandidate, Method candidate) {
		int uniqueCandidateParameterCount = uniqueCandidate.getParameterCount();
		int candidateParameterCount = candidate.getParameterCount();
		return (uniqueCandidateParameterCount != candidateParameterCount ||
				!Arrays.equals(uniqueCandidate.getParameterTypes(), candidate.getParameterTypes()));
	}

	/**
	 * 获取 factoryClass 所有候选方法包括父类的
	 * 根据 mbd 是否允许访问非公共构造函数和方法 标记 RootBeanDefinition#isNonPublicAccessAllowed
	 *
	 * Retrieve all candidate methods for the given class, considering
	 * the {@link RootBeanDefinition#isNonPublicAccessAllowed()} flag.
	 * Called as the starting point for factory method determination.
	 */
	private Method[] getCandidateMethods(Class<?> factoryClass, RootBeanDefinition mbd) {
		// 如果有系统安全管理器
		if (System.getSecurityManager() != null) {
			// 使用特权方式执行:如果mbd允许访问非公共构造函数和方法，就返回factoryClass子类和其父类的所有声明方法，首先包括子类方法；
			// 否则只获取factoryClass的public级别方法
			return AccessController.doPrivileged((PrivilegedAction<Method[]>) () ->
					(mbd.isNonPublicAccessAllowed() ?
						ReflectionUtils.getAllDeclaredMethods(factoryClass) : factoryClass.getMethods()));
		}
		else {
			// 如果mbd允许访问非公共构造函数和方法，就返回factoryClass子类和其父类的所有声明方法，首先包括子类方法；
			// 否则只获取factoryClass的public级别方法
			return (mbd.isNonPublicAccessAllowed() ?
					ReflectionUtils.getAllDeclaredMethods(factoryClass) : factoryClass.getMethods());
		}
	}

	/**
	 * Instantiate the bean using a named factory method. The method may be static, if the
	 * bean definition parameter specifies a class, rather than a "factory-bean", or
	 * an instance variable on a factory object itself configured using Dependency Injection.
	 * <p>Implementation requires iterating over the static or instance methods with the
	 * name specified in the RootBeanDefinition (the method may be overloaded) and trying
	 * to match with the parameters. We don't have the types attached to constructor args,
	 * so trial and error is the only way to go here. The explicitArgs array may contain
	 * argument values passed in programmatically via the corresponding getBean method.
	 * @param beanName the name of the bean
	 * @param mbd the merged bean definition for the bean
	 * @param explicitArgs argument values passed in programmatically via the getBean
	 * method, or {@code null} if none (-> use constructor argument values from bean definition)
	 * @return a BeanWrapper for the new instance
	 */
	public BeanWrapper instantiateUsingFactoryMethod(
			String beanName, RootBeanDefinition mbd, @Nullable Object[] explicitArgs) {

		// 新建一个 BeanWrapperImpl 实例，用于封装使用工厂方法生成的与 beanName 对应的 bean 对象
		BeanWrapperImpl bw = new BeanWrapperImpl();
		// 初始化实例包装类
		this.beanFactory.initBeanWrapper(bw);

		// 获取工厂 bean 对象，工厂 bean 对象的类对象，确定工厂方式是否为静态
		// 定义一个用于存放工厂 Bean 对象的 Object
		Object factoryBean;
		// 定义一个用于存放工厂 Bean 对象的类对象的 Class
		Class<?> factoryClass;
		// 定义一个标识：是否是静态工厂方法
		boolean isStatic;

		// 从 mbd 中获取配置的 FactoryBean 名
		String factoryBeanName = mbd.getFactoryBeanName();
		// 如果 factoryBeanName 不为 null
		if (factoryBeanName != null) {
			// 如果 factoryBean 名与 beanName 相同
			if (factoryBeanName.equals(beanName)) {
				throw new BeanDefinitionStoreException(mbd.getResourceDescription(), beanName,
						"factory-bean reference points back to the same bean definition");
			}
			// 从 bean 工厂中获取 factoryBeanName 所指的 factoryBean 对象
			factoryBean = this.beanFactory.getBean(factoryBeanName);
			// 如果 mbd 配置为单例作用域，且 beanName 已经在 bean 工厂的单例对象的 Map 中
			if (mbd.isSingleton() && this.beanFactory.containsSingleton(beanName)) {
				// 这个时候意味着 bean 工厂中已经有 beanName 的 Bean 对象，而这个还要多生成一个 beanName 的 Bean 对象，导致了冲突，抛出异常
				throw new ImplicitlyAppearedSingletonException();
			}
			// 获取 factoryBean 的 class 对象
			factoryClass = factoryBean.getClass();
			// 设置 isStatic 为 false，表示不是静态方法
			isStatic = false;
		}
		else {
			// It's a static factory method on the bean class.
			// 这是 bean 类上的静态工厂方法
			if (!mbd.hasBeanClass()) {
				// 如果 mbd 指定 bean 类
				throw new BeanDefinitionStoreException(mbd.getResourceDescription(), beanName,
						"bean definition declares neither a bean class nor a factory-bean reference");
			}
			// 将 factoryBean 设为 null
			factoryBean = null;
			// 指定 factoryClass 引用 mbd 指定的 bean 类
			factoryClass = mbd.getBeanClass();
			// 设置 isStatic 为 true，表示是静态方法
			isStatic = true;
		}

		// 尝试从 mbd 的缓存属性中获取要使用的静态方法，要使用的参数值数组

		// 准备使用的工厂方法
		Method factoryMethodToUse = null;
		// 准备使用的参数包装
		ArgumentsHolder argsHolderToUse = null;
		// 准备使用的参数
		Object[] argsToUse = null;

		if (explicitArgs != null) {
			// 如果没有显示参数，就使用这些参数
			argsToUse = explicitArgs;
		}
		else {
			// 解析出的参数
			Object[] argsToResolve = null;
			// 使用mbd的构造函数字段通用锁进行加锁，以保证线程安全
			synchronized (mbd.constructorArgumentLock) {
				// 指定factoryMethodToUser引用mbd已解析的构造函数或工厂方法对象
				factoryMethodToUse = (Method) mbd.resolvedConstructorOrFactoryMethod;
				// 如果有工厂方法，且构造函数已经解析了
				if (factoryMethodToUse != null && mbd.constructorArgumentsResolved) {
					// Found a cached factory method...
					// 找到了缓存的工厂方法
					// 指定argsToUser引用mbd完全解析的构造函数参数值
					argsToUse = mbd.resolvedConstructorArguments;
					if (argsToUse == null) {
						// 如果argsToUse为null，指定argsToResolve引用mbd部分准备好的构造函数参数值
						argsToResolve = mbd.preparedConstructorArguments;
					}
				}
			}
			// 如果argsToResolve不为null,即表示mbd还没有完全解析的构造函数参数值
			if (argsToResolve != null) {
				// 解析缓存在mbd中准备好的参数值,允许在没有此类BeanDefintion的时候回退
				argsToUse = resolvePreparedArguments(beanName, mbd, bw, factoryMethodToUse, argsToResolve, true);
			}
		}

		// 如果没解析过，就获取 factoryClass 的用户定义类型，因为此时 factoryClass 可能是 CGLIB 动态代理的类型
		// 所以要获取父类的类型，如果工厂方法是唯一的，就是没有方法重载，就获取解析的工厂方法；如果不为空，就添加到一个不可变列表里
		// 如果为空的话，需要去找出 factoryClass 以及父类的所有方法，进一步找出方法修饰符一致且名字跟工厂方法名字相同的且是 bean 注解的方法，并放入列表里
		if (factoryMethodToUse == null || argsToUse == null) {
			// Need to determine the factory method...
			// Try all methods with this name to see if they match the given arguments.
			// 获取用户定义的类
			factoryClass = ClassUtils.getUserClass(factoryClass);

			// 方法集合
			List<Method> candidates = null;
			// 如果工厂方法是唯一的，没有方法重载
			if (mbd.isFactoryMethodUnique) {
				if (factoryMethodToUse == null) {
					// 获取解析的工厂方法
					factoryMethodToUse = mbd.getResolvedFactoryMethod();
				}
				// 存在的话，返回仅包含 factoryMethodToUser 的不可变列表
				if (factoryMethodToUse != null) {
					candidates = Collections.singletonList(factoryMethodToUse);
				}
			}
			// 如果没有找到工厂方法，可能有方法重载
			if (candidates == null) {
				candidates = new ArrayList<>();


				// 获取 factoryClass 以及父类的所有方法作为候选的方法
				Method[] rawCandidates = getCandidateMethods(factoryClass, mbd);
				// 过滤出修饰符一样，工厂方法名一样 且是 bean 注解的方法
				for (Method candidate : rawCandidates) {
					// 如果 isStatic 修饰符一样且名字跟工厂方法名一样就添加
					if (Modifier.isStatic(candidate.getModifiers()) == isStatic && mbd.isFactoryMethod(candidate)) {
						candidates.add(candidate);
					}
				}
			}

			// 如果只获取到一个方法，且传入的参数为空，且没有设置构造方法参数值
			if (candidates.size() == 1 && explicitArgs == null && !mbd.hasConstructorArgumentValues()) {
				// 获取方法
				Method uniqueCandidate = candidates.get(0);
				// 如果没有参数
				if (uniqueCandidate.getParameterCount() == 0) {
					// 设置工厂方法
					mbd.factoryMethodToIntrospect = uniqueCandidate;
					synchronized (mbd.constructorArgumentLock) {
						// 设置解析出来的方法
						mbd.resolvedConstructorOrFactoryMethod = uniqueCandidate;
						mbd.constructorArgumentsResolved = true;
						mbd.resolvedConstructorArguments = EMPTY_ARGS;
					}
					bw.setBeanInstance(instantiate(beanName, mbd, factoryBean, uniqueCandidate, EMPTY_ARGS));
					return bw;
				}
			}

			// 如果有多个工厂方法，进行排序
			if (candidates.size() > 1) {  // explicitly skip immutable singletonList
				candidates.sort(AutowireUtils.EXECUTABLE_COMPARATOR);
			}

			// 构造器参数值
			ConstructorArgumentValues resolvedValues = null;
			boolean autowiring = (mbd.getResolvedAutowireMode() == AutowireCapableBeanFactory.AUTOWIRE_CONSTRUCTOR);
			// 最小类型差距
			int minTypeDiffWeight = Integer.MAX_VALUE;
			// 模糊的工厂方法集合
			Set<Method> ambiguousFactoryMethods = null;

			// 最小参数个数
			int minNrOfArgs;
			if (explicitArgs != null) {
				// 如果存在显示参数，就是显示参数的个数
				minNrOfArgs = explicitArgs.length;
			}
			else {
				// We don't have arguments passed in programmatically, so we need to resolve the
				// arguments specified in the constructor arguments held in the bean definition.
				// 如果存在构造器参数值，就解析出最小参数个数
				if (mbd.hasConstructorArgumentValues()) {
					ConstructorArgumentValues cargs = mbd.getConstructorArgumentValues();
					resolvedValues = new ConstructorArgumentValues();
					minNrOfArgs = resolveConstructorArguments(beanName, mbd, bw, cargs, resolvedValues);
				}
				else {
					// 没有就为 0
					minNrOfArgs = 0;
				}
			}

			LinkedList<UnsatisfiedDependencyException> causes = null;

			// 遍历每个候选的方法
			for (Method candidate : candidates) {
				int parameterCount = candidate.getParameterCount();

				if (parameterCount >= minNrOfArgs) {
					ArgumentsHolder argsHolder;

					Class<?>[] paramTypes = candidate.getParameterTypes();
					// 显示参数存在，如果长度不对，直接下一个，否则就创建参数持有其持有
					if (explicitArgs != null) {
						// Explicit arguments given -> arguments length must match exactly.
						if (paramTypes.length != explicitArgs.length) {
							continue;
						}
						argsHolder = new ArgumentsHolder(explicitArgs);
					}
					else {
						// Resolved constructor arguments: type conversion and/or autowiring necessary.
						try {
							String[] paramNames = null;
							ParameterNameDiscoverer pnd = this.beanFactory.getParameterNameDiscoverer();
							// 存在的话进行探测
							if (pnd != null) {
								paramNames = pnd.getParameterNames(candidate);
							}
							argsHolder = createArgumentArray(beanName, mbd, resolvedValues, bw,
									paramTypes, paramNames, candidate, autowiring, candidates.size() == 1);
						}
						catch (UnsatisfiedDependencyException ex) {
							if (logger.isTraceEnabled()) {
								logger.trace("Ignoring factory method [" + candidate + "] of bean '" + beanName + "': " + ex);
							}
							// Swallow and try next overloaded factory method.
							if (causes == null) {
								causes = new LinkedList<>();
							}
							causes.add(ex);
							continue;
						}
					}

					// 根据参数类型匹配，获取类型的差异值
					// mbd支持的构造函数解析模式,默认使用宽松模式:
					// 1. 严格模式如果摸棱两可的构造函数在转换参数时都匹配，则抛出异常
					// 2. 宽松模式将使用"最接近类型匹配"的构造函数
					// 如果bd支持的构造函数解析模式时宽松模式,引用获取类型差异权重值，否则引用获取Assignabliity权重值
					int typeDiffWeight = (mbd.isLenientConstructorResolution() ?
							argsHolder.getTypeDifferenceWeight(paramTypes) : argsHolder.getAssignabilityWeight(paramTypes));
					// Choose this factory method if it represents the closest match.
					// 保存最小的，说明参数类型相近
					if (typeDiffWeight < minTypeDiffWeight) {
						factoryMethodToUse = candidate;
						argsHolderToUse = argsHolder;
						// 让 argToUse 引用 argsHolder 的经过转换后参数值数组
						argsToUse = argsHolder.arguments;
						minTypeDiffWeight = typeDiffWeight;
						ambiguousFactoryMethods = null;
					}
					// Find out about ambiguity: In case of the same type difference weight
					// for methods with the same number of parameters, collect such candidates
					// and eventually raise an ambiguity exception.
					// However, only perform that check in non-lenient constructor resolution mode,
					// and explicitly ignore overridden methods (with the same parameter signature).
					// 如果出现类型差异相同，参数个数也相同，而且需要严格判断，参数长度也一样，常数类型也一样，就可能会无法判断要实例化哪个个，就会报异常

					// 找出歧义:如果具有相同数量参数的方法具有相同的类型差异权重，则收集此类候选想并最终引发歧义异常。
					// 但是，仅在非宽松构造函数解析模式下执行该检查，并显示忽略的方法（具有相同的参数签名）
					// 如果factoryMethodToUse不为null且typeDiffWeight与minTypeDiffWeight相等
					// 且mbd指定了严格模式解析构造函数且paramTypes的数组长度与factoryMethodToUse的参数数组长度相等且
					// paramTypes的数组元素与factoryMethodToUse的参数数组元素不相等
					else if (factoryMethodToUse != null && typeDiffWeight == minTypeDiffWeight &&
							!mbd.isLenientConstructorResolution() &&
							paramTypes.length == factoryMethodToUse.getParameterCount() &&
							!Arrays.equals(paramTypes, factoryMethodToUse.getParameterTypes())) {
						if (ambiguousFactoryMethods == null) {
							// 初始化ambiguousFactoryMethods为LinkedHashSet实例
							ambiguousFactoryMethods = new LinkedHashSet<>();
							// 将factoryMethodToUse添加到ambiguousFactoryMethods中
							ambiguousFactoryMethods.add(factoryMethodToUse);
						}
						// 将candidate添加到ambiguousFactoryMethods中
						ambiguousFactoryMethods.add(candidate);
					}
				}
			}

			// 整合无法筛选出候选方法或者无法解析出要使用的参数值的情况，抛出BeanCreationException并加以描述
			if (factoryMethodToUse == null || argsToUse == null) {
				if (causes != null) {
					// 从 cause 中移除最新的UnsatisfiedDependencyException
					UnsatisfiedDependencyException ex = causes.removeLast();
					for (Exception cause : causes) {
						// 将cause添加到该Bean工厂的抑制异常列表【{@link DefaultSingletonBeanRegistry#suppressedExceptions】中
						this.beanFactory.onSuppressedException(cause);
					}
					throw ex;
				}
				// 定义一个用于存放参数类型的简单类名的ArrayList对象，长度为minNrOfArgs
				List<String> argTypes = new ArrayList<>(minNrOfArgs);
				if (explicitArgs != null) {
					for (Object arg : explicitArgs) {
						// 如果arg不为null，将arg的简单类名添加到argTypes中；否则将"null"添加到argTypes中
						argTypes.add(arg != null ? arg.getClass().getSimpleName() : "null");
					}
				}
				else if (resolvedValues != null) {
					// 定义一个用于存放resolvedValues的泛型参数值和方法参数值的LinkedHashSet对象
					Set<ValueHolder> valueHolders = new LinkedHashSet<>(resolvedValues.getArgumentCount());
					// 将resolvedValues的方法参数值添加到valueHolders中
					valueHolders.addAll(resolvedValues.getIndexedArgumentValues().values());
					// 将resolvedValues的泛型参数值添加到valueHolders中
					valueHolders.addAll(resolvedValues.getGenericArgumentValues());
					// 遍历valueHolders，元素为value
					for (ValueHolder value : valueHolders) {
						// 如果value的参数类型不为null，就获取该参数类型的简单类名；否则(如果value的参数值不为null，即获取该参数值的简单类名;否则为"null")
						String argType = (value.getType() != null ? ClassUtils.getShortName(value.getType()) :
								(value.getValue() != null ? value.getValue().getClass().getSimpleName() : "null"));
						// 将argType添加到argTypes中
						argTypes.add(argType);
					}
				}
				// 将argType转换成字符串，以","隔开元素.用于描述Bean创建异常
				String argDesc = StringUtils.collectionToCommaDelimitedString(argTypes);
				// 抛出BeanCreationException:找不到匹配的工厂方法：工厂Bean'mbd.getFactoryBeanName()';工厂方法
				// 'mbd.getFactoryMethodName()(argDesc)'.检查是否存在具体指定名称和参数的方法，并且该方法时静态/非静态的.
				throw new BeanCreationException(mbd.getResourceDescription(), beanName,
						"No matching factory method found: " +
						(mbd.getFactoryBeanName() != null ?
							"factory bean '" + mbd.getFactoryBeanName() + "'; " : "") +
						"factory method '" + mbd.getFactoryMethodName() + "(" + argDesc + ")'. " +
						"Check that a method with the specified name " +
						(minNrOfArgs > 0 ? "and arguments " : "") +
						"exists and that it is " +
						(isStatic ? "static" : "non-static") + ".");
			}
			// 如果 factoryMethodToUse 是无返回值方法
			else if (void.class == factoryMethodToUse.getReturnType()) {
				throw new BeanCreationException(mbd.getResourceDescription(), beanName,
						"Invalid factory method '" + mbd.getFactoryMethodName() +
						"': needs to have a non-void return type!");
			}
			else if (ambiguousFactoryMethods != null) {
				throw new BeanCreationException(mbd.getResourceDescription(), beanName,
						"Ambiguous factory method matches found in bean '" + beanName + "' " +
						"(hint: specify index/type/name arguments for simple parameters to avoid type ambiguities): " +
						ambiguousFactoryMethods);
			}

			// 将筛选出来的工厂方法和解析出来的参数值缓存到 mbd 中
			if (explicitArgs == null && argsHolderToUse != null) {
				// 让mbd的唯一方法候选【{@link RootBeanDefinition#factoryMethodToIntrospect}】引用factoryMethodToUse
				mbd.factoryMethodToIntrospect = factoryMethodToUse;
				// 将argsHolderToUse所得到的参数值属性缓存到mbd对应的属性中
				argsHolderToUse.storeCache(mbd, factoryMethodToUse);
			}
		}

		// 使用 factoryBean 生成与 beanName 对应的 Bean 对象,并将该 Bean 对象保存到 bw 中
		bw.setBeanInstance(instantiate(beanName, mbd, factoryBean, factoryMethodToUse, argsToUse));
		return bw;
	}

	/**
	 * 使用 factoryBean 生成与 beanName 对应的 bean对象
	 * @param beanName
	 * @param mbd
	 * @param factoryBean
	 * @param factoryMethod
	 * @param args
	 * @return
	 */
	private Object instantiate(String beanName, RootBeanDefinition mbd,
			@Nullable Object factoryBean, Method factoryMethod, Object[] args) {

		try {
			// 如果有安全管理器
			if (System.getSecurityManager() != null) {
				// 使用特权方式运行：在beanFactory中返回beanName的Bean实例，并通过factoryMethod创建它
				return AccessController.doPrivileged((PrivilegedAction<Object>) () ->
						this.beanFactory.getInstantiationStrategy().instantiate(
								mbd, beanName, this.beanFactory, factoryBean, factoryMethod, args),
						this.beanFactory.getAccessControlContext());
			}
			else {
				// 获取实例化策略进行实例化（通过 factoryBean 实例化）
				return this.beanFactory.getInstantiationStrategy().instantiate(
						mbd, beanName, this.beanFactory, factoryBean, factoryMethod, args);
			}
		}
		// 捕捉所有实例化对象过程中的异常
		catch (Throwable ex) {
			// 抛出BeanCreationException:通过工厂方法实例化Bean失败
			throw new BeanCreationException(mbd.getResourceDescription(), beanName,
					"Bean instantiation via factory method failed", ex);
		}
	}

	/**
	 * 将 cargs 解析后的值保存到 resolveValues 中，并返回解析后的最小（索引参数数值+泛型参数数值）
	 *
	 * Resolve the constructor arguments for this bean into the resolvedValues object.
	 * This may involve looking up other beans.
	 * <p>This method is also used for handling invocations of static factory methods.
	 */
	private int resolveConstructorArguments(String beanName, RootBeanDefinition mbd, BeanWrapper bw,
			ConstructorArgumentValues cargs, ConstructorArgumentValues resolvedValues) {

		// 获取类型转换器，为 null，就引用 bw 的
		TypeConverter customConverter = this.beanFactory.getCustomTypeConverter();
		TypeConverter converter = (customConverter != null ? customConverter : bw);
		// BeanDefinitionValueResolver 值解析器：在 bean 工厂实现中使用 Helper 类，它将 beanDefinition 对象中包含的值解析为应用于目标 bean 实例的实际值
		BeanDefinitionValueResolver valueResolver =
				new BeanDefinitionValueResolver(this.beanFactory, beanName, mbd, converter);

		// 返回此实例中保存的参数值的数量，同时计算索引参数值和泛型参数值
		// 获取 cargs 的参数值数量和泛型参数值数量作为 最小(索引参数值数+泛型参数值数)
		int minNrOfArgs = cargs.getArgumentCount();

		// ConstructorArgumentValues.ValueHolder：构造函数参数值的Holder,带有可选的type属性，指示实际构造函数参数的目标类型
		// 遍历cargs所封装的索引参数值的Map，元素为entry(key=参数值的参数索引,value=ConstructorArgumentValues.ValueHolder对象)
		for (Map.Entry<Integer, ConstructorArgumentValues.ValueHolder> entry : cargs.getIndexedArgumentValues().entrySet()) {
			// 获取参数值的参数索引
			int index = entry.getKey();
			if (index < 0) {
				throw new BeanCreationException(mbd.getResourceDescription(), beanName,
						"Invalid constructor argument index: " + index);
			}
			// 索引大于最小参数值数量
			if (index + 1 > minNrOfArgs) {
				minNrOfArgs = index + 1;
			}
			ConstructorArgumentValues.ValueHolder valueHolder = entry.getValue();
			// 如果 valueHolder 已经包含转换后的值，将index和valueHolder添加到resolvedValues所封装的索引参数值的Map中
			if (valueHolder.isConverted()) {
				resolvedValues.addIndexedArgumentValue(index, valueHolder);
			}
			else {
				// 使用valueResolver解析出valueHolder实例的构造函数参数值所封装的对象
				Object resolvedValue =
						valueResolver.resolveValueIfNecessary("constructor argument", valueHolder.getValue());
				// 使用valueHolder所封装的type,name属性以及解析出来的resolvedValue构造出一个ConstructorArgumentValues.ValueHolder对象
				ConstructorArgumentValues.ValueHolder resolvedValueHolder =
						new ConstructorArgumentValues.ValueHolder(resolvedValue, valueHolder.getType(), valueHolder.getName());
				// 将valueHolder作为resolvedValueHolder的配置源对象设置到resolvedValueHolder中
				resolvedValueHolder.setSource(valueHolder);
				// 将index和valueHolder添加到resolvedValues所封装的索引参数值的Map中
				resolvedValues.addIndexedArgumentValue(index, resolvedValueHolder);
			}
		}

		// 遍历cargs的泛型参数值的列表,元素为ConstructorArgumentValues.ValueHolder对象
		for (ConstructorArgumentValues.ValueHolder valueHolder : cargs.getGenericArgumentValues()) {
			// 如果valueHolder已经包含转换后的值
			if (valueHolder.isConverted()) {
				// 将index和valueHolder添加到resolvedValues的泛型参数值的列表中
				resolvedValues.addGenericArgumentValue(valueHolder);
			}
			else {
				// 使用valueResolver解析出valueHolder实例的构造函数参数值所封装的对象
				Object resolvedValue =
						valueResolver.resolveValueIfNecessary("constructor argument", valueHolder.getValue());
				// 使用valueHolder所封装的type,name属性以及解析出来的resovledValue构造出一个ConstructorArgumentValues.ValueHolder对象
				ConstructorArgumentValues.ValueHolder resolvedValueHolder = new ConstructorArgumentValues.ValueHolder(
						resolvedValue, valueHolder.getType(), valueHolder.getName());
				// 将valueHolder作为resolvedValueHolder的配置源对象设置到resolvedValueHolder中
				resolvedValueHolder.setSource(valueHolder);
				// 将index和valueHolder添加到resolvedValues所封装的索引参数值的Map中
				resolvedValues.addGenericArgumentValue(resolvedValueHolder);
			}
		}

		// 返回最小(索引参数值数+泛型参数值数)
		return minNrOfArgs;
	}

	/**
	 * 给定已解析的构造函数参数值，创建一个参数数组以调用构造函数或工厂方法
	 * Create an array of arguments to invoke a constructor or factory method,
	 * given the resolved constructor argument values.
	 */
	private ArgumentsHolder createArgumentArray(
			String beanName, RootBeanDefinition mbd, @Nullable ConstructorArgumentValues resolvedValues,
			BeanWrapper bw, Class<?>[] paramTypes, @Nullable String[] paramNames, Executable executable,
			boolean autowiring, boolean fallback) throws UnsatisfiedDependencyException {

		// 获取类型转换器
		TypeConverter customConverter = this.beanFactory.getCustomTypeConverter();
		TypeConverter converter = (customConverter != null ? customConverter : bw);

		// 参数持有器，保存解析后的参数值
		ArgumentsHolder args = new ArgumentsHolder(paramTypes.length);
		// 构造器参数值集合（存储构造函数参数值），用于查找下一个任意泛型参数值时，忽略该集合的元素的 HashSet
		Set<ConstructorArgumentValues.ValueHolder> usedValueHolders = new HashSet<>(paramTypes.length);
		// 存储自动注入的 bean 名字
		Set<String> autowiredBeanNames = new LinkedHashSet<>(4);

		// 如果有参数
		for (int paramIndex = 0; paramIndex < paramTypes.length; paramIndex++) {
			Class<?> paramType = paramTypes[paramIndex];
			// 获取参数名字
			String paramName = (paramNames != null ? paramNames[paramIndex] : "");
			// Try to find matching constructor argument value, either indexed or generic.
			// 尝试找到匹配的构造函数参数值，无论是索引的还是泛型的,定义一个用于存储与paramIndex对应的ConstructorArgumentValues.ValueHolder实例
			ConstructorArgumentValues.ValueHolder valueHolder = null;
			if (resolvedValues != null) {
				// 在 resolvedValues 中查找与 paramIndex 对应的参数值，或者按 paramType 匹配
				valueHolder = resolvedValues.getArgumentValue(paramIndex, paramType, paramName, usedValueHolders);
				// If we couldn't find a direct match and are not supposed to autowire,
				// let's try the next generic, untyped argument value as fallback:
				// it could match after type conversion (for example, String -> int).
				// 如果找不到直接匹配并且不希望自动装配，请尝试使用一个通用的，无类型的参数值作为后备：
				// 类型转换后可以匹配(例如String -> int)
				// 如果valueHolder为null 且 (mbd不支持使用构造函数进行自动注入 或者 paramTypes数组长度与resolvedValues的(索引参数值+泛型参数值)数量相等)
				if (valueHolder == null && (!autowiring || paramTypes.length == resolvedValues.getArgumentCount())) {
					// 在resovledValues中查找任意，不按名称匹配参数值的下一个泛型参数值，而忽略usedValueHolders参数值
					valueHolder = resolvedValues.getGenericArgumentValue(null, null, usedValueHolders);
				}
			}
			if (valueHolder != null) {
				// We found a potential match - let's give it a try.
				// Do not consider the same value definition multiple times!
				// 我们找到了可能的匹配-让我们尝试一些。
				// 不要考虑相同的值定义
				// 将valueHolder添加到usedValueHolders中，以表示该valueHolder已经使用过，下次在resolvedValues中
				// 获取下一个valueHolder时，不要返回同一个对象
				usedValueHolders.add(valueHolder);
				// 从valueHolder中获取原始参数值
				Object originalValue = valueHolder.getValue();
				// 定义一个用于存储转换后的参数值的Object对象
				Object convertedValue;
				// valueHolder 已经包含转换后的值
				if (valueHolder.isConverted()) {
					// 从 valueHolder 获取已经转换后的值
					convertedValue = valueHolder.getConvertedValue();
					// 将转换后的值保存到 args 的 preparedArgument 数组 的 paramIndex 元素中
					args.preparedArguments[paramIndex] = convertedValue;
				}
				else {
					// 将 executable 中 paramIndex 对应的参数封装成 MethodParameter 对象
					MethodParameter methodParam = MethodParameter.forExecutable(executable, paramIndex);
					try {
						// 使用converter将originalValue转换为paramType类型
						convertedValue = converter.convertIfNecessary(originalValue, paramType, methodParam);
					}
					// 捕捉在转换类型时出现的类型不匹配异常
					catch (TypeMismatchException ex) {
						throw new UnsatisfiedDependencyException(
								mbd.getResourceDescription(), beanName, new InjectionPoint(methodParam),
								"Could not convert argument value of type [" +
										ObjectUtils.nullSafeClassName(valueHolder.getValue()) +
										"] to required type [" + paramType.getName() + "]: " + ex.getMessage());
					}
					// 获取 valueHolder 的源对象，一般是ValueHolder
					Object sourceHolder = valueHolder.getSource();
					// 如果sourceHolder是ConstructorArgumentValues.ValueHolder实例
					if (sourceHolder instanceof ConstructorArgumentValues.ValueHolder) {
						// 将sourceHolder转换为ConstructorArgumentValues.ValueHolder对象
						Object sourceValue = ((ConstructorArgumentValues.ValueHolder) sourceHolder).getValue();
						// 将args的resolveNecessary该为true，表示args.preparedArguments需要解析
						args.resolveNecessary = true;
						// 将sourceValue保存到args的preparedArguments数组的paramIndex对应元素中
						args.preparedArguments[paramIndex] = sourceValue;
					}
				}
				// 将convertedValue保存到args的arguments数组的paramIndex对应元素中
				args.arguments[paramIndex] = convertedValue;
				// 将originalValue保存到args的rawArguments数组的paramIndex对应元素中
				args.rawArguments[paramIndex] = originalValue;
			}
			else {
				// 否则(valueHolder不为null)
				// 将executable中paramIndex对应的参数封装成MethodParameter对象
				MethodParameter methodParam = MethodParameter.forExecutable(executable, paramIndex);
				// No explicit match found: we're either supposed to autowire or
				// have to fail creating an argument array for the given constructor.
				// 找不到明确的匹配项:我们要么自动装配，要么必须为给定的构造函数创建参数数组而失败
				// mbd不支持适用构造函数进行自动注入
				if (!autowiring) {
					// 抛出不满足依赖异常:类型为[paramType.getName]的参数的参数值不明确-您是否指定了正确的bean引用作为参数？
					throw new UnsatisfiedDependencyException(
							mbd.getResourceDescription(), beanName, new InjectionPoint(methodParam),
							"Ambiguous argument values for parameter of type [" + paramType.getName() +
							"] - did you specify the correct bean references as arguments?");
				}
				try {
					// 解析应该自动装配的methodParam的Bean对象,使用autowiredBeanNames保存所找到的所有候选Bean对象
					Object autowiredArgument = resolveAutowiredArgument(
							methodParam, beanName, autowiredBeanNames, converter, fallback);
					// 将autowiredArgument保存到args的rawArguments数组的paramIndex对应元素中
					args.rawArguments[paramIndex] = autowiredArgument;
					// 将autowiredArgument保存到args的arguments数组的paramIndex对应元素中
					args.arguments[paramIndex] = autowiredArgument;
					// 将autowiredArgumentMarker保存到args的arguments数组的paramIndex对应元素中
					args.preparedArguments[paramIndex] = autowiredArgumentMarker;
					// 将args的resolveNecessary该为true，表示args.preparedArguments需要解析
					args.resolveNecessary = true;
				}
				// 捕捉解析应该自动装配的methodParam的Bean对象时出现的BeanException
				catch (BeansException ex) {
					throw new UnsatisfiedDependencyException(
							mbd.getResourceDescription(), beanName, new InjectionPoint(methodParam), ex);
				}
			}
		}

		// 遍历autowiredBeanNames，元素为autowiredBeanName
		for (String autowiredBeanName : autowiredBeanNames) {
			// 注册beanName与dependentBeanNamed的依赖关系到beanFactory中
			this.beanFactory.registerDependentBean(autowiredBeanName, beanName);
			if (logger.isDebugEnabled()) {
				logger.debug("Autowiring by type from bean name '" + beanName +
						"' via " + (executable instanceof Constructor ? "constructor" : "factory method") +
						" to bean named '" + autowiredBeanName + "'");
			}
		}

		// 将args(保存着解析后的参数值的ArgumentsHolder对象)返回出去
		return args;
	}

	/**
	 * 解析缓存在 mbd 中准备好的参数值
	 *
	 * Resolve the prepared arguments stored in the given bean definition.
	 */
	private Object[] resolvePreparedArguments(String beanName, RootBeanDefinition mbd, BeanWrapper bw,
			Executable executable, Object[] argsToResolve, boolean fallback) {

		// 获取 bean 工厂自定义的类型转换器
		TypeConverter customConverter = this.beanFactory.getCustomTypeConverter();
		// 自定义转换器为空，引用 bw
		TypeConverter converter = (customConverter != null ? customConverter : bw);

		// BeanDefinitionValueResolver 解析器：主要用于将 bean 定义对象中包含的值解析应用于目标 bean 实例的实际值
		BeanDefinitionValueResolver valueResolver =
				new BeanDefinitionValueResolver(this.beanFactory, beanName, mbd, converter);
		// executable 中获取其参数类型
		Class<?>[] paramTypes = executable.getParameterTypes();

		// 定义一个解析后的参数数组
		Object[] resolvedArgs = new Object[argsToResolve.length];
		for (int argIndex = 0; argIndex < argsToResolve.length; argIndex++) {
			Object argValue = argsToResolve[argIndex];
			// 为 executable 的 argIndex 位置参数创建一个新的 MethodParameter 对象
			MethodParameter methodParam = MethodParameter.forExecutable(executable, argIndex);
			// 如果 argValue 是自动装配的参数标记
			if (argValue == autowiredArgumentMarker) {
				// 解析出应该自动装配的 methodParam 的 Bean 对象
				argValue = resolveAutowiredArgument(methodParam, beanName, null, converter, fallback);
			}
			// BeanMetadataElement 对象：由包含配置源对象的 bean 元数据元素实现的接口，BeanDefinition 的父接口
			else if (argValue instanceof BeanMetadataElement) {
				// 交由 valueResolver 解析出 value 所封装的对象
				argValue = valueResolver.resolveValueIfNecessary("constructor argument", argValue);
			}
			else if (argValue instanceof String) {
				// 评估 beanDefinition 中包含的 argValue,如果 argValue 是可解析表达式，会对其进行解析，否则得到的还是 argValue
				argValue = this.beanFactory.evaluateBeanDefinitionString((String) argValue, mbd);
			}
			Class<?> paramType = paramTypes[argIndex];
			try {
				// 将argValue转换为paramType类型对象并赋值给第i个resolvedArgs元素
				resolvedArgs[argIndex] = converter.convertIfNecessary(argValue, paramType, methodParam);
			}
			// 转换类型时，类型不匹配
			catch (TypeMismatchException ex) {
				throw new UnsatisfiedDependencyException(
						mbd.getResourceDescription(), beanName, new InjectionPoint(methodParam),
						"Could not convert argument value of type [" + ObjectUtils.nullSafeClassName(argValue) +
						"] to required type [" + paramType.getName() + "]: " + ex.getMessage());
			}
		}
		// 返回解析后的参数数组
		return resolvedArgs;
	}

	protected Constructor<?> getUserDeclaredConstructor(Constructor<?> constructor) {
		Class<?> declaringClass = constructor.getDeclaringClass();
		Class<?> userClass = ClassUtils.getUserClass(declaringClass);
		if (userClass != declaringClass) {
			try {
				return userClass.getDeclaredConstructor(constructor.getParameterTypes());
			}
			catch (NoSuchMethodException ex) {
				// No equivalent constructor on user class (superclass)...
				// Let's proceed with the given constructor as we usually would.
			}
		}
		return constructor;
	}

	/**
	 * 模板方法用于解析应该自动装配的指定参数的 bean
	 *
	 * Template method for resolving the specified argument which is supposed to be autowired.
	 */
	@Nullable
	protected Object resolveAutowiredArgument(MethodParameter param, String beanName,
			@Nullable Set<String> autowiredBeanNames, TypeConverter typeConverter, boolean fallback) {

		// 获取 param 参数类型
		Class<?> paramType = param.getParameterType();
		// InjectionPoint 用于描述一个 AOP 注入点,如果 paramType 属于 InjectionPoint
		if (InjectionPoint.class.isAssignableFrom(paramType)) {
			// 从线程本地中获取当前切入点对象，该对象一般在 Bean 工厂解析出与 descriptor 所包装的对象匹配的候选 Bean 对象的时候设置
			InjectionPoint injectionPoint = currentInjectionPoint.get();
			if (injectionPoint == null) {
				// 抛出非法状态异常：
				throw new IllegalStateException("No current InjectionPoint available for " + param);
			}
			return injectionPoint;
		}
		try {
			// DependencyDescriptor：即将注入的特定依赖项描述符。包装构造函数，方法参数或字段，以允许对其元数据 的统一访问
			// 该DependencyDescriptor对象的依赖类型就是指param的类型
			// 将param封装成DependencyDescriptor对象，让当前Bean工厂根据该DependencyDescriptor对象的依赖类型解析出与
			// 该DependencyDescriptor对象所包装的对象匹配的候选Bean对象，然后返回出去
			return this.beanFactory.resolveDependency(
					new DependencyDescriptor(param, true), beanName, autowiredBeanNames, typeConverter);
		}
		// 捕捉没有唯一的BeanDefinition异常
		catch (NoUniqueBeanDefinitionException ex) {
			throw ex;
		}
		catch (NoSuchBeanDefinitionException ex) {
			// 如果可在没有此类BeanDefinition的时候回退
			if (fallback) {
				// Single constructor or factory method -> let's return an empty array/collection
				// for e.g. a vararg or a non-null List/Set/Map parameter.
				// 单一构造函数或工厂方法->让我们返回一个空数组/集合，例如vararg或非null的List/Set/Map对象
				// 如果参数类型是数组类型
				if (paramType.isArray()) {
					// 根据参数数组的元素类型新建一个空数组对象
					return Array.newInstance(paramType.getComponentType(), 0);
				}
				// 如果paramType是否是常见的Collection类
				else if (CollectionFactory.isApproximableCollectionType(paramType)) {
					// 根据参数类型创建对应的Collection对象
					return CollectionFactory.createCollection(paramType, 0);
				}
				// 如果paramType是否是常见的Map类
				else if (CollectionFactory.isApproximableMapType(paramType)) {
					// 根据参数类型创建对应的Map对象
					return CollectionFactory.createMap(paramType, 0);
				}
			}
			// 不可以回退，或者参数类型不是常见数组/集合类型时，重新抛出异常
			throw ex;
		}
	}

	/**
	 * 设置新的当前切入点对象，返回旧的当前切入点对象
	 * @param injectionPoint
	 * @return
	 */
	static InjectionPoint setCurrentInjectionPoint(@Nullable InjectionPoint injectionPoint) {
		// 从线程本地的当前切入点对象中获取旧的 InjectionPoint 对象
		InjectionPoint old = currentInjectionPoint.get();
		if (injectionPoint != null) {
			// 不为 null，将 injectionPoint 设置成当前切入点对象
			currentInjectionPoint.set(injectionPoint);
		}
		else {
			// 移除当前切入点对象
			currentInjectionPoint.remove();
		}
		// 返回旧的切入点对象
		return old;
	}


	/**
	 * 用于保存参数组合的私有内部类
	 * Private inner class for holding argument combinations.
	 */
	private static class ArgumentsHolder {

		// 原始参数数组
		public final Object[] rawArguments;

		// 经过转换后的参数数组
		public final Object[] arguments;

		// 准备好的参数数组，保存着 由解析的自动装配参数替换的标记和原参数值
		public final Object[] preparedArguments;

		// 需要解析的标记，默认为 false
		public boolean resolveNecessary = false;

		public ArgumentsHolder(int size) {
			this.rawArguments = new Object[size];
			this.arguments = new Object[size];
			this.preparedArguments = new Object[size];
		}

		public ArgumentsHolder(Object[] args) {
			this.rawArguments = args;
			this.arguments = args;
			this.preparedArguments = args;
		}

		/**
		 * 获取类型差异权重，宽容模式下使用
		 * 	1、获取表示paramTypes和arguments之间的类层次结构差异的权重【变量 typeDiffWeight】
		 * 	2、获取表示paramTypes和rawArguments之间的类层次结构差异的权重【变量 rawTypeDiffWeight】
		 * 	3、比较typeDiffWeight和rawTypeDiffWeight取最小权重并返回出去，但是还是以原始类型优先，因为差异值还-1024
		 *
		 * @param paramTypes
		 * @return
		 */
		public int getTypeDifferenceWeight(Class<?>[] paramTypes) {
			// If valid arguments found, determine type difference weight.
			// Try type difference weight on both the converted arguments and
			// the raw arguments. If the raw weight is better, use it.
			// Decrease raw weight by 1024 to prefer it over equal converted weight.
			// 如果找到有效的参数，请确定类型差异权重。尝试对转换后的参数和原始参数都使用类型差异权重。如果
			// 原始重量更好，请使用它。将原始重量减少1024，以使其优于相等的转换重量。
			// MethodInvoker.getTypeDifferenceWeight-确定表示类型和参数之间的类层次结构差异的权重：
			// 1. arguments的类型不paramTypes类型的子类，直接返回 Integer.MAX_VALUE,最大重量，也就是直接不匹配
			// 2. paramTypes类型是arguments类型的父类则+2
			// 3. paramTypes类型是arguments类型的接口，则+1
			// 4. arguments的类型直接就是paramTypes类型,则+0
			// 获取表示paramTypes和arguments之间的类层次结构差异的权重
			int typeDiffWeight = MethodInvoker.getTypeDifferenceWeight(paramTypes, this.arguments);
			// 获取表示paramTypes和rawArguments之间的类层次结构差异的权重
			int rawTypeDiffWeight = MethodInvoker.getTypeDifferenceWeight(paramTypes, this.rawArguments) - 1024;
			// 取最小权重，但是还是以原始类型优先，因为差异值还-1024
			return Math.min(rawTypeDiffWeight, typeDiffWeight);
		}

		/**
		 * 获取 Assignability 权重，严格模式下使用
		 * @param paramTypes
		 * @return
		 */
		public int getAssignabilityWeight(Class<?>[] paramTypes) {
			for (int i = 0; i < paramTypes.length; i++) {
				// 如果确定 arguments 不是paramTypes的实例
				if (!ClassUtils.isAssignableValue(paramTypes[i], this.arguments[i])) {
					// 返回Integer最大值，意味着既然连最终的转换后参数值都不能匹配，这个情况下的paramTypes所对应的工厂方法是不可以接受的
					return Integer.MAX_VALUE;
				}
			}
			for (int i = 0; i < paramTypes.length; i++) {
				// 如果确定rawArguments不是paramTypes的实例
				if (!ClassUtils.isAssignableValue(paramTypes[i], this.rawArguments[i])) {
					// 返回Integer最大值-512，意味着虽然转换后的参数值匹配，但是原始的参数值不匹配，这个情况下的paramTypes所对应的工厂方法还是可以接受的
					return Integer.MAX_VALUE - 512;
				}
			}
			// 在完全匹配的情况下，返回Integer最大值-1024；意味着因为最终的转换后参数值和原始参数值都匹配，这种情况下paramTypes所对应的工厂方法非常可以接收
			return Integer.MAX_VALUE - 1024;
		}

		/**
		 * 将 ArgumentsHolder 所得到的参数值属性缓存到 mbd 对应的属性中
		 * @param mbd
		 * @param constructorOrFactoryMethod
		 */
		public void storeCache(RootBeanDefinition mbd, Executable constructorOrFactoryMethod) {
			// 使用mbd的构造函数通用锁【{@link RootBeanDefinition#constructorArgumentLock}】加锁以保证线程安全
			synchronized (mbd.constructorArgumentLock) {
				// 让mbd的已解析的构造函数或工厂方法【{@link RootBeanDefinition#resolvedConstructorOrFactoryMethod}】引用constructorOrFactoryMethod
				mbd.resolvedConstructorOrFactoryMethod = constructorOrFactoryMethod;
				// 将mdb的构造函数参数已解析标记【{@link RootBeanDefinition#constructorArgumentsResolved}】设置为true
				mbd.constructorArgumentsResolved = true;
				// 为 true ，表示参数还需要进一步解析
				if (this.resolveNecessary) {
					// 让mbd的缓存部分准备好的构造函数参数值属性【{@link RootBeanDefinition#preparedConstructorArguments}】引用preparedArguments
					mbd.preparedConstructorArguments = this.preparedArguments;
				}
				else {
					// 让mbd的缓存完全解析的构造函数参数属性【{@link RootBeanDefinition#resolvedConstructorArguments}】引用arguments
					mbd.resolvedConstructorArguments = this.arguments;
				}
			}
		}
	}


	/**
	 * 用于检查Java 6的{@link ConstructorProperties}注解的委托类
	 *
	 * Delegate for checking Java 6's {@link ConstructorProperties} annotation.
	 */
	private static class ConstructorPropertiesChecker {

		/**
		 * 获取 candidate 的 ConstructorProperties 注解的 name 属性值
		 * @param candidate
		 * @param paramCount
		 * @return
		 */
		@Nullable
		public static String[] evaluate(Constructor<?> candidate, int paramCount) {
			ConstructorProperties cp = candidate.getAnnotation(ConstructorProperties.class);
			if (cp != null) {
				// 获取cp指定的getter方法的属性名
				String[] names = cp.value();
				if (names.length != paramCount) {
					throw new IllegalStateException("Constructor annotated with @ConstructorProperties but not " +
							"corresponding to actual number of parameters (" + paramCount + "): " + candidate);
				}
				return names;
			}
			else {
				// 没有配置 ConstructorProperties 注解，则返回null
				return null;
			}
		}
	}

}
