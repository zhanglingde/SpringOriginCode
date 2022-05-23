import com.ling.test02.Test02;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.aop.TargetSource;
import org.springframework.aop.aspectj.AspectJAfterAdvice;
import org.springframework.aop.aspectj.AspectJExpressionPointcut;
import org.springframework.aop.aspectj.annotation.AnnotationAwareAspectJAutoProxyCreator;
import org.springframework.aop.aspectj.annotation.BeanFactoryAspectJAdvisorsBuilder;
import org.springframework.aop.aspectj.annotation.MetadataAwareAspectInstanceFactory;
import org.springframework.aop.framework.ProxyCreatorSupport;
import org.springframework.aop.framework.autoproxy.BeanFactoryAdvisorRetrievalHelper;
import org.springframework.beans.*;
import org.springframework.beans.factory.config.*;
import org.springframework.beans.factory.support.*;
import org.springframework.beans.factory.xml.*;
import org.springframework.context.annotation.*;
import org.springframework.context.support.*;

import com.ling.test02.MyClassPathXmlApplicationContext;
import org.springframework.core.ResolvableType;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.io.Resource;
import org.springframework.core.type.AnnotatedTypeMetadata;
import org.springframework.util.StringValueResolver;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;

import org.springframework.context.expression.StandardBeanExpressionResolver;
import org.springframework.beans.factory.annotation.CustomAutowireConfigurer;
import org.springframework.context.event.EventListenerMethodProcessor;
import org.springframework.beans.factory.support.BeanNameGenerator;
import org.springframework.beans.factory.annotation.AutowiredAnnotationBeanPostProcessor;
import org.springframework.beans.factory.annotation.InitDestroyAnnotationBeanPostProcessor;
import org.springframework.core.env.PropertyResolver;
import org.springframework.context.support.AbstractRefreshableConfigApplicationContext;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.context.config.ContextNamespaceHandler;
import org.springframework.beans.support.ResourceEditorRegistrar;
import org.springframework.aop.framework.autoproxy.AbstractAutoProxyCreator;
import org.springframework.aop.aspectj.autoproxy.AspectJAwareAdvisorAutoProxyCreator;


import com.ling.test09.selfbdrpp.MyBeanDefinitionRegistryPostProcessor;
import com.ling.test06.customtag.CatBeanDefinitionParser;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;


public class Readme {

	/**
	 * 1. 流程概述： {@link com.ling.test01.Test}  <br>
	 * 2. 启动流程细节    {@link Test02}			<br>
	 *
	 * refresh 执行前设置：
	 * <ul>
	 *     <li> 调用父类方法初始化资源： {@link AbstractApplicationContext#AbstractApplicationContext()} </li>
	 *     <li> 设置配置文件路径：
	 * 	         {@link AbstractRefreshableConfigApplicationContext#setConfigLocations(String...)}</li>
	 *     <li> spel 表达式（${username}）解析：配置文件，环境变量都是通过同样的方式进行解析的 {@link PropertyResolver#resolveRequiredPlaceholders(String)}</li>
	 *     <li> 设置表达式解析器：{@link AbstractApplicationContext#prepareBeanFactory(ConfigurableListableBeanFactory)} </li>
	 * </ul>
	 *
	 */
	void read01() {
	}

	/**
	 * <ol>
	 *     <li>
	 *         refresh()：{@link AbstractApplicationContext#refresh()}
	 *     </li>
	 *     <li>
	 *         	初始化属性值扩展,自定义环境变量，重写该方法
	 *         	<ul>
	 * 	 			<li> {@link AbstractApplicationContext#initPropertySources } </li>
	 * 	 			<li> {@link MyClassPathXmlApplicationContext#initPropertySources() DefaultListableBeanFactory } </li>
	 * 	   		</ul>
	 *     </li>
	 *     <li>
	 *         监听器初始化： this.earlyApplicationListeners  (SpringBoot 启动配合理解,SpringBoot 启动时会有很多监听器，放到 earlyApplicationListeners容器中)
	 *     </li>
	 *     <li>
	 *         设置 bean 允许被覆盖，允许循环依赖，重写方法
	 *         {@link AbstractRefreshableApplicationContext#customizeBeanFactory(DefaultListableBeanFactory)}
	 *     </li>
	 *     <li>
	 *         初始化 documentReader，为 xml 文件解析做准备：{@link AbstractRefreshableApplicationContext#loadBeanDefinitions}
	 *     </li>
	 * </ol>
	 *
	 *
	 */
	void read02(){}

	/**
	 * 05. xml 配置文件加载过程 -> beanDefinition(一个标签解析成一个 BeanDefinition)
	 * <ul>
	 *     <li> GenericBeanDefinition </li>
	 *     <li> RootBeanDefinition </li>
	 *     <li> ScannedGenericBeanDefinition（扫描的 Bean） </li>
	 *     <li> AnnotatedBeanDefinition </li>
	 * </ul>
	 *
	 *
	 * 资源文件读取加载：
	 * <ol>
	 *     <li> 解析配置文件为 Resource： {@link org.springframework.core.io.support.PathMatchingResourcePatternResolver#getResources(String) } </li>
	 *     <li> 从网络加载配置文件，本地定义的文件 spring-bean/META-iNF  spring.schemas :{@link AbstractXmlApplicationContext#loadBeanDefinitions(XmlBeanDefinitionReader)}</li>
	 *     <li> 将 Resource 解析为 BeanDefinition： {@link XmlBeanDefinitionReader#loadBeanDefinitions(Resource...) } </li>
	 *     <li> 将 Resource 解析为 BeanDefinition： {@link XmlBeanDefinitionReader#doLoadBeanDefinitions(InputSource, Resource) } </li>
	 *     <li> 获取 Document： 			{@link XmlBeanDefinitionReader#doLoadDocument(InputSource, Resource)}  <br>
	 *     		EntityResolver 用法：{@link org.xml.sax.EntityResolver}
	 *     </li>
	 *     <li> 将 Document 解析为 BeanDefinition  {@link DefaultBeanDefinitionDocumentReader#doRegisterBeanDefinitions(Element)} </li>
	 *     <li> 标签解析 {@link XmlBeanDefinitionReader#registerBeanDefinitions(Document, Resource)} </li>
	 *     <li> 默认标签及自定义标签解析： {@link DefaultBeanDefinitionDocumentReader#parseBeanDefinitions(Element, BeanDefinitionParserDelegate)}</li>
	 *     <li> bean 标签解析生成 BeanDefinition：{@link BeanDefinitionParserDelegate#parseBeanDefinitionElement(Element, BeanDefinition)} </li>
	 *     <li> 注册 BeanDefinition（放入 BeanFactory）： {@link BeanDefinitionReaderUtils#registerBeanDefinition(BeanDefinitionHolder, BeanDefinitionRegistry)} </li>
	 * </ol>
	 *
	 */
	void read05() {
	}

	/**
	 * 06. 自定义标签加载过程     <br><br>
	 *
	 * 标签元素解析器抽象类：{@link BeanDefinitionParser}
	 * <ul>
	 *     <li> context 命名空间 Handler：{@link ContextNamespaceHandler } </li>
	 *     <li> property-placeholder 标签的元素解析器：{@link org.springframework.context.config.PropertyPlaceholderBeanDefinitionParser} </li>
	 *     <li> 自定义 cat 标签的元素解析器： 	{@link CatBeanDefinitionParser} </li>
	 * </ul>
	 *
     *
	 * context:property-placeholder 标签解析：
	 * <ol>
	 *     <li> 解析自定义标签： {@link DefaultBeanDefinitionDocumentReader#parseBeanDefinitions} <br>
	 *     		{@link BeanDefinitionParserDelegate#parseCustomElement(Element)}
	 *     </li>
	 *     <li> createReaderContext(resource) 创建 XmlReaderContext，读取 spring.handlers 中对应命名空间的 Handler： {@link XmlBeanDefinitionReader#registerBeanDefinitions(Document, Resource)} </li>
	 *     <li> readerContext 获取 NamespaceHandlerResolver 解析命名空间，根据命名空间获得对应命名空间标签的 Handler：{@link XmlReaderContext#getNamespaceHandlerResolver() } </li>
	 *     <li> handle 根据标签名(property-placeholder)获得对应的解析器： {@link NamespaceHandlerSupport#parse(Element, ParserContext)}  </li>
	 *     <li> 将标签属性值转换成对象（放入 beanDefinition）：{@link AbstractSingleBeanDefinitionParser#parseInternal}中的 doParser 方法进行解析 </li>
	 *     <li> 将解析后的对象注册成 beanDefinition：{@link AbstractBeanDefinitionParser#parse}   registerBeanDefinition </li>
	 * </ol>
	 *
	 * 			{@link org.springframework.context.config.PropertyPlaceholderBeanDefinitionParser}
	 * 			{@link PropertySourcesPlaceholderConfigurer}
	 *
	 * 获取命名空间 url -> 根据命名空间找到对应的 Handler（spring.handlers）-> ContextNamespaceHandler（初始化解析器）->
	 * 通过反射将 Handler 实例化存在 handlerMappings 中 -> Handler init 方法为每个属性值创建不同解析器（）
	 *
	 * 非默认命名空间加载过程：
	 * <ol>
	 *     <li> 加载 spring.handlers 配置文件 </li>
	 *     <li> 将配置文件内容加载到 handlerMappings 集合中 </li>
	 *     <li> 根据指定的 key 获取对应的处理器（一个处理器处理一种标签） </li>
	 * </ol>
	 * 自定义标签解析步骤：
	 * <ol>
	 *     <li> 创建一个对应的解析器处理类（在 init 方法中添加 parser 类） </li>
	 *     <li> 创建一个普通的 spring.handlers 配置文件，让应用程序能够完成加载工作 </li>
	 *     <li> 创建对应标签的 parser 类（对当前标签的其他属性值进行解析工作） </li>
	 * </ol>
	 */
	void read06() {}

	/**
	 * 07. BeanFactory的准备工作
	 * <p>
	 * 初始化 beanFactory 工厂，对各种属性进行填充
	 * {@link AbstractApplicationContext#prepareBeanFactory}
	 * <p>
	 * {@link ComponentScanBeanDefinitionParser} 扫描注解注入类
	 *
	 * <ul>
	 *     <li> spel 表达式解析类：{@link StandardBeanExpressionResolver} </li>
	 * </ul>
	 *
	 * 2. 注册定制化解析器（扩展自定义属性编辑器 ）			<br>
	 * 		{@link PropertyEditorRegistrar#registerCustomEditors}
	 * 		{@link ResourceEditorRegistrar#registerCustomEditors(PropertyEditorRegistry)}
	 * <ol>
	 *     <li> 自定义一个实现 PropertyEditorRegistrar 接口的编辑器 </li>
	 *     <li> 自定义属性编辑器的注册器，实现 PropertyEditorRegistrar 接口 </li>
	 *     <li> 让 Spring 能够识别到对应的注册器 </li>
	 * </ol>
	 *
	 * 3. 扩展定义 MyAwareProcessor
	 * 其他忽略的 Aware（自定义的 Aware） 在 beanPostProcessor 中进行调用
	 * BeanNameAware,BeanClassLoaderAware,BeanFactoryAware 在 invokeAwareMethods 中进行调用
	 * {@link AbstractAutowireCapableBeanFactory#invokeAwareMethods(String, Object)}
	 *
	 */
	void read07() {}

	/**
	 * 8. BeanFactoryPostProcessor 的执行过程
	 * <p>
	 * 1. 获取到 beanFactory ,就可以对 工厂中所有属性进行操作  <br>
	 * postProcessBeanFactory 扩展  <br>
	 * {@link AbstractApplicationContext#postProcessBeanFactory(ConfigurableListableBeanFactory) }
	 * 		<ol>
	 * 		   <li>  先执行 BeanDefinitionRegistry 类型的 beanFactory;先遍历实现 PriorityOrdered 接口的，然后是实现 Order 接口的，最后是无序的</li>
	 * 		   <li>  然后执行不属于 BeanDefinitionRegistry 类型的，直接执行 postProcessBeanFactory 方法</li>
	 * 		   <li>  找到所有实现 BeanFactoryPostProcessor 接口的类</li>
	 * 		</ol>
	 * </p>
	 *
	 * <p>
	 * 		2. BeanFactoryPostProcessor 与 BeanDefinitionRegistryPostProcessor 的区别 <br>
	 *        {@link AbstractApplicationContext#invokeBeanFactoryPostProcessors(ConfigurableListableBeanFactory) }
	 * 		<ul>
	 * 		    <li> {@link BeanFactoryPostProcessor }：后置处理器（增强器），对 beanFactory 中属性进行修改 </li>
	 * 		    <li> {@link BeanDefinitionRegistryPostProcessor }：BeanFactoryPostProcessor 子类，  </li>
	 * 		    <li> {@link BeanDefinitionRegistry}：提供 BeanDefinition 的增删改查  </li>
	 *
	 * 		    <li> @Configuration、@Autowired、JSR-250 中的 @Resource 等注解 {@link AnnotationConfigUtils#registerAnnotationConfigProcessors(BeanDefinitionRegistry, Object)   } </li>
	 * 		</ul>
	 * </p>
	 *
	 * <p>
	 * 3. 自带的 BeanFactoryPostProcessor 子类
	 *
	 * <ul>
	 *       <li>  最重要的后置处理器 例：@Configuration 是后置处理器进行处理的 {@link  ConfigurationClassPostProcessor}
	 *       	打开 <context:component-scan></context:component-scan> 才会被扫描到
	 *                                                                {@link ComponentScanBeanDefinitionParser#registerComponents(XmlReaderContext, Set, Element)}
	 *       </li>
	 *       <li>  自定义自动注入 										{@link  CustomAutowireConfigurer} </li>
	 *       <li>  自定义编辑器 											{@link  CustomEditorConfigurer} </li>
	 *       <li>  @EventListener 支持 									{@link  EventListenerMethodProcessor} </li>
	 *       <li> 用于解析 bean 定义中属性值里面的占位符 					{@link PlaceholderConfigurerSupport } </li>
	 * </ul>
	 */
	void read08() {}


	/**
	 * 9. BeanFactoryPostProcessor 的执行过程2
	 * <p>
	 *     <ol>
	 *         <li>  自定义 BeanDefinitionRegistryPostProcessor，分别实现 PriorityOrdered、Ordered 接口和不实现接口，BDRPP 被扫描执行的顺序不同 <br>
	 * <p>
	 *             {@link MyBeanDefinitionRegistryPostProcessor}
	 *         </li>
	 *         <li> 每个阶段执行 BDRPP ，每次需要重新获取 BeanDefinitionRegistryPostProcessor
	 *
	 *         </li>
	 *     </ol>
	 * </p>
	 * <p>
	 *     BeanDefinitionRegistry :对 bean 进行增删改查操作
	 * </p>
	 *
	 * <p>
	 * 		扫描 @Component 注册的Bean 	 <br>
	 *        {@link ComponentScanBeanDefinitionParser#registerComponents(XmlReaderContext, Set, Element)}
	 * <p>
	 * <p> 注解的扫描	 <br>
	 *     {@link ConfigurationClassPostProcessor#processConfigBeanDefinitions(BeanDefinitionRegistry)}
	 *
	 * 		<li> @Configuration </li>
	 * 		<li> @Bean </li>
	 * 		<li> @Import </li>
	 * </p>
	 *
	 * <p> @Import、@ComponentScan、@ComponentScans、@ImportResource 等注解的解析
	 * 	<ul>
	 * 	    <li> 该类的子类实现是 BeanName 的生成方式  <br>
	 *            {@link BeanNameGenerator}
	 * 	    </li>
	 * 	    <li> @Conditional 注解处理	<br>
	 *            {@link ConditionEvaluator#shouldSkip(AnnotatedTypeMetadata)}
	 * 	    </li>
	 * 	    <li> 	{@link ConfigurationClassParser#doProcessConfigurationClass(ConfigurationClass, ConfigurationClassParser.SourceClass, Predicate)}
	 * 	    </li>
	 * 	</ul>
	 * </p>
	 */
	void read09() {
	}

	/**
	 * 10. ConfigurationClassPostProcessor
	 * <ol>
	 *
	 * <li>		@Component、@PropertySource、@ComponentScan、@Import、@ImportResource、@Bean 等注解的解析    <br>
	 *        {@link ConfigurationClassParser#doProcessConfigurationClass(ConfigurationClass, ConfigurationClassParser.SourceClass, Predicate)  doProcessConfigurationClass}
	 * </li>
	 * <li> @Component 注解解析  <br>
	 *        {@link com.ling.test10.MyComponentScan}   <br>
	 *        {@link ConfigurationClassParser#processMemberClasses(ConfigurationClass, ConfigurationClassParser.SourceClass, Predicate)  processMemberClasses}
	 * </li>
	 * <li> @PropertySource 注解 @Value 注解 spel 表达式解析    <br>
	 *        {@link com.ling.test10.MyPropertySource}        <br>
	 *        {@link ConfigurationClassParser#processPropertySource(AnnotationAttributes)}
	 * </li>
	 * <li> @Bean 注解解析 和 @Conditional 条件标签  <br>
	 *        {@link com.ling.test10.BeanConfig}  <br>
	 *        {@link ConfigurationClassParser#processConfigurationClass(ConfigurationClass, Predicate)}  <br>
	 * 		this.conditionEvaluator.shouldSkip
	 *
	 * </li>
	 * <li> 注解修饰的类解析成 BeanDefinition  <br>
	 *        {@link ConfigurationClassParser#parse(Set)}
	 *
	 * </li>
	 * <li> asm  <br>
	 * <li> @Import 注解解析  todo SpringBoot 自动装配
	 *
	 * </li>
	 * <li> asm   <br>
	 *        {@link ConfigurationClassParser#retrieveBeanMethodMetadata(ConfigurationClassParser.SourceClass)}
	 *
	 * </li>
	 * </ol>
	 */
	void read10() {
	}

	/**
	 * 11. 注册 BeanPostProcessor
	 *
	 * <ol>
	 *     <li> @Resource 注解处理（JSR-250）
	 *         {@link AnnotationConfigUtils#registerAnnotationConfigProcessors(BeanDefinitionRegistry, Object)} )}
	 *     </li>
	 *     <li>
	 *         {@link ComponentScanBeanDefinitionParser}
	 *     </li>
	 *     <li> BeanPostProcessor 的子类：   <br>
	 *        销毁相关，BeanFactory 注释生命周期上： {@link org.springframework.beans.factory.config.DestructionAwareBeanPostProcessor DestructionAwareBeanPostProcessor}  <br>
	 *        合并BeanDefinition， {@link org.springframework.beans.factory.support.MergedBeanDefinitionPostProcessor MergedBeanDefinitionPostProcessor}  <br>
	 * 		  bean 实例化相关： {@link org.springframework.beans.factory.config.InstantiationAwareBeanPostProcessor InstantiationAwareBeanPostProcessor}  <br>
	 * 		  循环依赖： {@link org.springframework.beans.factory.config.SmartInstantiationAwareBeanPostProcessor SmartInstantiationAwareBeanPostProcessor}
	 *     </li>
	 * </ol>
	 */
	void read11() {
	}

	/**
	 * 12. spring 的消息资源和监听器的初始化
	 *
	 * <ol>
	 *     <li> 消息资源（国际化 i18n）  <br>
	 *         {@link AbstractApplicationContext#initMessageSource()}    <br>
	 *         {@link org.springframework.context.MessageSource MessageSource}   <br>
	 *         {@link org.springframework.context.support.DelegatingMessageSource DelegatingMessageSource}
	 *     </li>
	 * </ol>
	 */
	void read12() {
	}

	/**
	 * 13. Spring Bean
	 *
	 *
	 * <p> 类型转换器
	 * 		<ul>
	 * 		 	<li> {@link org.springframework.core.convert.converter.Converter Converter}
	 * 		 	</li>
	 * 		 	<li> {@link org.springframework.core.convert.converter.GenericConverter GenericConverter}
	 *
	 * 		 	</li>
	 *
	 * 		 	<li> {@link org.springframework.core.convert.converter.ConverterFactory ConverterFactory}
	 *
	 * 		 	</li>
	 * 		 	<li> {@link AbstractApplicationContext#finishBeanFactoryInitialization(ConfigurableListableBeanFactory) finishBeanFactoryInitialization}  自定义类型转换器</li>
	 * 		</ul>
	 *
	 * </p>
	 * <p>
	 *     值解析器
	 *     <ul>
	 *         <li>
	 *             {@link PlaceholderConfigurerSupport#doProcessProperties(ConfigurableListableBeanFactory, StringValueResolver) PlaceholderConfigurerSupport#doProcessProperties }
	 *         </li>
	 *     </ul>
	 *
	 * </p>
	 *
	 * <p>
	 *     RootBeanDefinition：将 beanDefinition 与父类合并（SpringMVC）   <br>
	 *     mergedBeanDefinitions RootBeanDefinition 缓存的初始化
	 *     <ol>
	 *         <li> doGetBeanNamesForType {@link DefaultListableBeanFactory#getBeanNamesForType(Class)}</li>
	 *         <li>从缓存中获取 BeanDefinition {@link org.springframework.beans.factory.support.AbstractBeanFactory#getMergedLocalBeanDefinition(String)}  <br>
	 *         	   添加到缓存中 invokeBeanFactoryPostProcessors -> getBeanNamesForType -> doGetBeanNamesForType -> getMergedLocalBeanDefinition
	 *         </li>
	 *     </ol>
	 * </p>
	 *
	 * <p> FactoryBean
	 *        {@link  AbstractBeanFactory#getObjectForBeanInstance(Object, String, String, RootBeanDefinition) getObjectForBeanInstance}
	 * </p>
	 */
	void read13() {
	}


	/**
	 * 14. Spring Bean 的创建流程二
	 * <br>
	 * mergedBeanDefinitions 缓存的创建
	 * <ol>
	 *     <li>{@link AbstractApplicationContext#invokeBeanFactoryPostProcessors(ConfigurableListableBeanFactory)}</li>
	 *     <li>{@link PostProcessorRegistrationDelegate#invokeBeanFactoryPostProcessors(ConfigurableListableBeanFactory, List)}</li>
	 *     <li>{@link DefaultListableBeanFactory#getBeanNamesForType(Class, boolean, boolean)}</li>
	 *     <li>{@link DefaultListableBeanFactory#doGetBeanNamesForType(ResolvableType, boolean, boolean)}</li>
	 *     <li>{@link DefaultListableBeanFactory#getMergedLocalBeanDefinition(String)}</li>
	 * </ol>
	 * <p>
	 * 创建 bean 的流程：getBean -> doGetBean -> createBean -> createBean
	 * <ol>
	 *     <li> {@link AbstractApplicationContext#finishBeanFactoryInitialization(ConfigurableListableBeanFactory)} </li>
	 *     <li> {@link AbstractBeanFactory#getBean(String, Class) } </li>
	 *     <li> {@link AbstractBeanFactory#doGetBean(String, Class, Object[], boolean)  } </li>
	 *     <li> {@link AbstractAutowireCapableBeanFactory#createBean(String, RootBeanDefinition, Object[])   } </li>
	 *     <li> ObjectFactory.getObject 调用 doCreateBean {@link AbstractAutowireCapableBeanFactory#doCreateBean(String, RootBeanDefinition, Object[])    } </li>
	 * </ol>
	 * <p>
	 * bean 创建的四种方式
	 *   <ol>
	 *       <li> new、反射、 factoryMethod、supplier</li>
	 *   </ol>
	 * <p>
	 * lookup-method、replace-method ：单例引用原型
	 *
	 * <ol>
	 *     <li>	{@link AbstractAutowireCapableBeanFactory#createBean(String, RootBeanDefinition, Object[])  mbdToUse.prepareMethodOverrides()}
	 *
	 *     </li>
	 * </ol>
	 */
	void read14() {

	}

	/**
	 * 15. Spring Bean 的创建流程三
	 * <p>
	 * Spring 创建 bean 的方式：
	 *
	 * <ol>
	 *     <li>BeanPostProcessor：createBean 中的 doCreateBean 并不一定会执行；取决于 BeanPostProcessor 中是否提前创建对象 <br>
	 *     	   createBean：{@link AbstractAutowireCapableBeanFactory#resolveBeforeInstantiation(String, RootBeanDefinition)}
	 *     </li>
	 *     <li> supplier 创建 bean   mbd.getInstanceSupplier()  <br>
	 *            {@link AbstractAutowireCapableBeanFactory#createBeanInstance(String, RootBeanDefinition, Object[])}    <br>
	 *            {@link com.ling.test15.supplier.SupplierBeanFactoryPostProcessor}
	 *     </li>
	 *     <li> 工厂方法创建 bean <br>
	 * 			可能有多个 getPerson 方法，而配置文件中只配置了  getPerson,所以会有许多判断（对参数值进行解析判断），判断逻辑比较复杂 <br>
	 *         {@link org.springframework.beans.factory.support.ConstructorResolver#instantiateUsingFactoryMethod(String, RootBeanDefinition, Object[])}
	 *     </li>
	 * </ol>
	 */
	void read15() {
	}

	/**
	 * 16. Spring bean 创建流程四 - 构造方法获取   <br>
	 * <p>
	 * <p>
	 * 构造函数注入、简单初始化
	 * <ul>
	 * 		<li>
	 *            {@link AbstractAutowireCapableBeanFactory#createBeanInstance(String, RootBeanDefinition, Object[])  createBeanInstance}
	 * 		</li>
	 * </ul>
	 * <p>
	 * <p>
	 * 反射创建对象：获取构造器（无参或有参），通过构造器实例化
	 *
	 * <ol>
	 *     构造器选择：如果有设置构造方法参数，根据参数选择有参构造器；如果没有设置构造器，则使用默认的无参构造（对构造器排序，减少构造器匹配操作）
	 *     <li>
	 *         {@link AbstractAutowireCapableBeanFactory#autowireConstructor(String, RootBeanDefinition, Constructor[], Object[]) autowireConstructor}
	 *     </li>
	 *     <li>
	 *            {@link ConstructorResolver#autowireConstructor(String, RootBeanDefinition, Constructor[], Object[])}
	 *     </li>
	 * </ol>
	 * <p>
	 * <p>
	 * Autowired 在构造方法上的处理
	 *
	 * <ol>
	 *     <li>
	 *         {@link AbstractAutowireCapableBeanFactory#determineConstructorsFromBeanPostProcessors(Class, String)}
	 *     </li>
	 *     <li>
	 *         {@link SmartInstantiationAwareBeanPostProcessor}
	 *     </li>
	 *     <li>
	 *         {@link AutowiredAnnotationBeanPostProcessor#determineCandidateConstructors(Class, String)}
	 *     </li>
	 *     <li>  @Primary  {@link  RootBeanDefinition#getPreferredConstructors() }
	 *     </li>
	 * </ol>
	 * <p>
	 * 实例化策略
	 * <ul>
	 *     <li>无参、有参、工厂方法{@link SimpleInstantiationStrategy}</li>
	 *     <li>动态代理对象无参、有参 {@link CglibSubclassingInstantiationStrategy}</li>
	 * </ul>
	 * <p>
	 * BeanWrapper 包装类：{@link BeanWrapper}
	 * <ul>
	 *     <li> 类型转换：TypeConverter</li>
	 *     <li> 属性编辑：PropertyEditorRegistry</li>
	 * </ul>
	 *
	 * <p>
	 *     @ PostConstruct 和 @PreDestroy 和 @Resource 的处理
	 * </p>
	 * <ul>
	 *     <li>
	 *            {@link AbstractAutowireCapableBeanFactory#applyMergedBeanDefinitionPostProcessors }
	 *     </li>
	 *     <li>
	 *         {@link MergedBeanDefinitionPostProcessor}
	 *     </li>
	 *     <li>
	 *         {@link CommonAnnotationBeanPostProcessor#postProcessMergedBeanDefinition(RootBeanDefinition, Class, String)}
	 *     </li>
	 *     <li>
	 *         {@link InitDestroyAnnotationBeanPostProcessor}
	 *     </li>
	 *     <li> 构建生命周期元数据： {@link InitDestroyAnnotationBeanPostProcessor#buildLifecycleMetadata(Class)}</li>
	 * </ul>
	 */
	void read16() {
	}


	/**
	 * 17. Spring bean 创建流程五
	 *
	 * <p> @Autowired、@Value、@Inject </p>
	 * <ul>
	 *     <li> 解析 @Autowired、@Value 注解 {@link AutowiredAnnotationBeanPostProcessor }  </li>
	 *     <li>  {@link org.springframework.beans.factory.annotation.AutowiredAnnotationBeanPostProcessor.AutowiredFieldElement AutowiredFieldElement }  </li>
	 *     <li>  {@link org.springframework.beans.factory.annotation.AutowiredAnnotationBeanPostProcessor.AutowiredMethodElement AutowiredMethodElement }  </li>
	 * </ul>
	 */
	void read17() {
	}


	/**
	 * 18. Spring Bean 创建流程六    <br>
	 * <p></p>
	 * bean 属性值设置：
	 * {@link AbstractAutowireCapableBeanFactory#populateBean(String, RootBeanDefinition, BeanWrapper) populateBean}
	 * <ol>
	 *     <li>按名称注入： {@link AbstractAutowireCapableBeanFactory#autowireByName(String, AbstractBeanDefinition, BeanWrapper, MutablePropertyValues) autowireByName} </li>
	 *     <ul>
	 *     		<li>寻找 bean 中需要依赖注入的属性： {@link AbstractAutowireCapableBeanFactory#unsatisfiedNonSimpleProperties(AbstractBeanDefinition, BeanWrapper)  unsatisfiedNonSimpleProperties} </li>
	 *     		<li>注册依赖： {@link AbstractAutowireCapableBeanFactory#registerDependentBean(String, String)  registerDependentBean} </li>
	 *     </ul>
	 *
	 *     <li>按类型注入： {@link AbstractAutowireCapableBeanFactory#autowireByType(String, AbstractBeanDefinition, BeanWrapper, MutablePropertyValues) autowireByType} </li>
	 *     <ul>
	 *     		<li>寻找 bean 中需要依赖注入的属性： {@link AbstractAutowireCapableBeanFactory#unsatisfiedNonSimpleProperties(AbstractBeanDefinition, BeanWrapper)   unsatisfiedNonSimpleProperties} </li>
	 *     		<li>寻找 bean 中需要依赖注入的属性： {@link AbstractAutowireCapableBeanFactory#resolveDependency(DependencyDescriptor, String)}   resolveDependency} </li>
	 *     		<li>寻找 bean 中需要依赖注入的属性： {@link AbstractAutowireCapableBeanFactory#registerDependentBean(String, String)}    registerDependentBean} </li>
	 *     </ul>
	 * </ol>
	 * <p>
	 * 将属性应用到 bean 中：
	 * {@link AbstractAutowireCapableBeanFactory#applyPropertyValues(String, BeanDefinition, BeanWrapper, PropertyValues) applyPropertyValues }
	 */
	void read18() {
	}

	/**
	 * 忽略三个 Aware 接口
	 * <ol>
	 *     <li> {@link AbstractRefreshableApplicationContext#refreshBeanFactory()}</li>
	 *     <li>忽略三个 Aware 接口： {@link AbstractRefreshableApplicationContext#createBeanFactory()} ()}</li>
	 * </ol>
	 * 忽略其他 Aware 接口：
	 * <ol>
	 *     <li> {@link AbstractApplicationContext#refresh()}</li>
	 *     <li>忽略 Aware 接口： {@link AbstractApplicationContext#prepareBeanFactory(ConfigurableListableBeanFactory)}</li>
	 *     <li>处理 Aware 接口： {@link ApplicationContextAwareProcessor#postProcessBeforeInitialization(Object, String) }  </li>
	 * </ol>
	 */
	void read19() {}

	/**
	 * Spring AOP 相关 BeanDefinition 的准备工作
	 * <ul>
	 *     <li> aop 标签解析：{@link org.springframework.aop.config.ConfigBeanDefinitionParser#parse(Element, ParserContext)}</li>
	 *     <li> 解析 aspect 标签：{@link org.springframework.aop.config.ConfigBeanDefinitionParser#parseAspect(Element, ParserContext)}</li>
	 *     <li> 解析 advice 节点并注册到 bean 工厂中：{@link org.springframework.aop.config.ConfigBeanDefinitionParser#parseAdvice(String, int, Element, Element, ParserContext, List, List)}</li>
	 *     <li> 解析 point-cut 节点并注册到 bean 工厂中：{@link org.springframework.aop.config.ConfigBeanDefinitionParser#parsePointcut(Element, ParserContext)}</li>
	 * </ul>
	 *
	 * AOP
	 * <ol>
	 *     <li> AOP 标签命名空间解析器：  	{@link org.springframework.aop.config.AopNamespaceHandler} </li>
	 *     <li> AOP 标签命名空间解析器：  	{@link org.springframework.aop.config.AspectJAutoProxyBeanDefinitionParser#parse(Element, ParserContext)} </li>
	 *     <li> 注册 AnnotationAwareAspectJAutoProxyCreator：  	{@link org.springframework.aop.config.AopNamespaceUtils#registerAspectJAnnotationAutoProxyCreatorIfNecessary(ParserContext, Element)} </li>
	 *     <li> 注册 AnnotationAwareAspectJAutoProxyCreator：  	{@link org.springframework.aop.config.AopConfigUtils#registerOrEscalateApcAsRequired(Class, BeanDefinitionRegistry, Object)} </li>
	 *     <li> AnnotationAwareAspectJAutoProxyCreator 实现 BeanPostProcessor,调用父类的 postProcessAfterInitialization 方法  	</li>
	 *     <li> 创建 AOP 代理 ：  	{@link org.springframework.aop.framework.autoproxy.AbstractAutoProxyCreator#postProcessAfterInitialization(Object, String)} </li>
	 * </ol>
	 */
	void read21(){}

	/**
	 * Spring AOP 核心对象的创建
	 * Advisor 使用 构造方法创建
	 *
	 * <ol>
	 *     <li> 创建 bean ：{@link AbstractAutowireCapableBeanFactory#createBean(String, RootBeanDefinition, Object[])   } </li>
	 *     <li> 创建 bean ：{@link AbstractAutowireCapableBeanFactory#resolveBeforeInstantiation(String, RootBeanDefinition)  } </li>
	 *     <li> 创建 bean ：{@link AbstractAutoProxyCreator#postProcessBeforeInstantiation(Class, String)  } </li>
	 *     <li> 创建 bean ：{@link AspectJAwareAdvisorAutoProxyCreator#shouldSkip(Class, String)  } </li>
	 *     <li> 获取 Advisor，创建 Advisor 所需要的 Bean：{@link BeanFactoryAdvisorRetrievalHelper#findAdvisorBeans()} </li>
	 * </ol>
	 *
	 * 注解方式创建 AOP 代理
	 *
	 * <ol>
	 *     <li> bean 实例化前调用：  {@link AnnotationAwareAspectJAutoProxyCreator#postProcessAfterInitialization(Object, String)} </li>
	 *     <li> 调用父类： {@link AbstractAutoProxyCreator#postProcessAfterInitialization(Object, String)  } </li>
	 *     <li> 获取增强方法：{@link org.springframework.aop.framework.autoproxy.AbstractAdvisorAutoProxyCreator#findEligibleAdvisors(Class, String) }  </li>
	 *     <li> 获取所有增强(获取 @Aspect 标注的 bean)：{@link AnnotationAwareAspectJAutoProxyCreator#findCandidateAdvisors()} </li>
	 *     <li> 获取 bean 的注解增强（获取 @Aspect 标注的 bean）：{@link BeanFactoryAspectJAdvisorsBuilder#buildAspectJAdvisors()} </li>
	 *     <li> 增强器的获取： {@link org.springframework.aop.aspectj.annotation.ReflectiveAspectJAdvisorFactory#getAdvisors(MetadataAwareAspectInstanceFactory)}</li>
	 *     <li> 普通增强器的获取：{@link org.springframework.aop.aspectj.annotation.ReflectiveAspectJAdvisorFactory#getAdvisor(Method, MetadataAwareAspectInstanceFactory, int, String)}</li>
	 *     <li> 根据切点信息生成增强，不同注解类型封装不同的增强器：{@link org.springframework.aop.aspectj.annotation.InstantiationModelAwarePointcutAdvisorImpl#instantiateAdvice(AspectJExpressionPointcut)   }</li>
	 *     <li> 前置处理增强器 MethodBeforeAdviceInterceptor 对应 AspectJMethodBeforeAdvice {@link org.springframework.aop.framework.adapter.MethodBeforeAdviceInterceptor#invoke(MethodInvocation)}</li>
	 *     <li> 后置处理器增强 AspectJAfterAdvice {@link AspectJAfterAdvice#invoke(MethodInvocation)} </li>
	 *     <li> 挑取出匹配通配符的增强器 {@link org.springframework.aop.framework.autoproxy.AbstractAdvisorAutoProxyCreator#findAdvisorsThatCanApply(List, Class, String)}</li>
	 *	   <li> 创建代理：	{@link AbstractAutoProxyCreator#createProxy(Class, String, Object[], TargetSource)  }</li>
	 *	   <li> 封装 Advisor：{@link AbstractAutoProxyCreator#buildAdvisors(String, Object[]) }</li>
	 *	   <li> 创建代理（AOP 或 CGLIB）： {@link ProxyCreatorSupport#createAopProxy()}  }  </li>
	 *	   <li> Jdk 获取代理：	{@link org.springframework.aop.framework.JdkDynamicAopProxy#invoke(Object, Method, Object[])  }</li>
	 *
	 *     <li> 增强同步实例化增强器  {@link org.springframework.aop.aspectj.annotation.ReflectiveAspectJAdvisorFactory.SyntheticInstantiationAdvisor}</li>
	 * </ol>
	 */
	void read22(){}

}


