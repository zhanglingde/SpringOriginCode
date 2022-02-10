import com.ling.test02.Test02;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionReaderUtils;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.xml.*;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.AbstractRefreshableApplicationContext;

import com.ling.test02.MyClassPathXmlApplicationContext;
import org.springframework.context.support.AbstractXmlApplicationContext;
import org.springframework.core.io.Resource;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

import org.springframework.context.annotation.ComponentScanBeanDefinitionParser;
import org.springframework.context.expression.StandardBeanExpressionResolver;
import org.springframework.beans.PropertyEditorRegistrar;
import org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.beans.factory.config.PlaceholderConfigurerSupport;
import org.springframework.context.annotation.AnnotationConfigUtils;
import org.springframework.context.annotation.ConfigurationClassPostProcessor;
import org.springframework.beans.factory.annotation.CustomAutowireConfigurer;
import org.springframework.beans.factory.config.CustomEditorConfigurer;
import org.springframework.context.event.EventListenerMethodProcessor;

import com.ling.test09.selfbdrpp.MyBeanDefinitionRegistryPostProcessor;

import java.util.Set;


public class Readme {

	/**
	 * 1. 流程概述 {@link com.ling.test.Test}
	 * 2. 启动流程细节    {@link Test02}
	 * spel 表达式（${username}）解析：配置文件，环境变量都是通过同样的方式进行解析的 <br>
	 * <p>
	 * <p>
	 * 设置配置路径：
	 * {@link org.springframework.context.support.AbstractRefreshableConfigApplicationContext#setConfigLocations(String...)}
	 * <p>
	 * refresh():
	 * {@link AbstractApplicationContext#refresh()}
	 * 1. 初始化属性值扩展,自定义环境变量，重写该方法
	 * {@link AbstractApplicationContext#initPropertySources}
	 * {@link MyClassPathXmlApplicationContext#initPropertySources()} (DefaultListableBeanFactory)}
	 * 2. 监听器初始化： this.earlyApplicationListeners  (SpringBoot 启动配合理解,SpringBoot 启动时会有很多监听器，放到 earlyApplicationListeners容器中)
	 * 3. 设置 bean 允许被覆盖，允许循环依赖，重写方法
	 * {@link AbstractRefreshableApplicationContext#customizeBeanFactory(DefaultListableBeanFactory)}
	 * 4. 初始化 documentReader，为 xml 文件解析做准备
	 * {@link AbstractRefreshableApplicationContext#loadBeanDefinitions}
	 */
	void readme01() {
	}

	/**
	 * 05. xml 配置文件加载过程 -> beanDefinition(一个标签解析成一个 BeanDefinition)
	 * 1. 从网络加载配置文件，本地定义的文件 spring-bean/META-iNF  spring.schemas
	 * <p>
	 * 2. 获取配置文件路径：
	 * {@link AbstractXmlApplicationContext#loadBeanDefinitions(XmlBeanDefinitionReader)}
	 * 3. 读取xml 配置文件，生成document对象
	 * {@link XmlBeanDefinitionReader#doLoadBeanDefinitions(InputSource, Resource)}
	 * 4. 解析过程
	 * {@link DefaultBeanDefinitionDocumentReader#doRegisterBeanDefinitions(Element)}
	 * 解析 bean标签 {@link DefaultBeanDefinitionDocumentReader#processBeanDefinition}
	 * 5. 将xml解析成 beanDefinition对象后，将 BeanDefinition 对象放入 BeanFactory
	 * beanDefinitionMap<beanName,BeanDefinition>
	 * beanDefinitionNames<beanName>
	 * {@link BeanDefinitionReaderUtils#registerBeanDefinition(BeanDefinitionHolder, BeanDefinitionRegistry)}
	 */
	void readme05() {
	}

	/**
	 * 06. 自定义标签加载过程
	 * <p>
	 * XmlReaderContext -> namespaceHandlerResolver
	 * 相关对象：
	 * {@link org.springframework.context.config.PropertyPlaceholderBeanDefinitionParser}
	 * {@link PropertySourcesPlaceholderConfigurer}
	 * 非默认命名空间加载过程：
	 * 1. 加载 spring.handlers 配置文件
	 * 2. 将配置文件内容加载到 handlerMappings 集合中
	 * 3. 根据指定的 key 获取对应的处理器（一个处理器处理一种标签）
	 * 自定义标签解析步骤：
	 * 1. 创建一个对应的解析器处理类（在 init 方法中添加 parser 类）
	 * 2. 创建一个普通的 spring.handlers 配置文件，让应用程序能够完成加载工作
	 * 3. 创建对应标签的 parser 类（对当前标签的其他属性值进行解析工作）
	 * context 标签解析：
	 * 1. createReaderContext(resource)：创建ReaderContext，读取 spring.handlers 命名空间的Handler
	 * {@link XmlBeanDefinitionReader#registerBeanDefinitions}
	 * 2. 解析导入的命名空间 <context><aop><tx> 等
	 * {@link DefaultBeanDefinitionDocumentReader#parseBeanDefinitions}
	 * {@link BeanDefinitionParserDelegate#parseCustomElement(Element, BeanDefinition)}
	 * 获取命名空间 url -> 根据命名空间找到对应的 Handler（spring.handlers）-> ContextNamespaceHandler（初始化解析器）->
	 * 通过反射将 Handler 实例化存在 handlerMappings 中 -> Handler init 方法为每个属性值创建不同解析器（）
	 * <ling:user> 标签需要一个 UserBeanDefinitionParser 的解析器
	 * <ling:book> 标签需要一个 BookBeanDefinitionParser 的解析器
	 * 3. 解析：将标签属性值转换成对象（放入 beanDefinition）
	 * {@link AbstractSingleBeanDefinitionParser#parseInternal}中的 doParser 方法进行解析
	 * 4. 将解析后的对象注册成 beanDefinition
	 * {@link AbstractBeanDefinitionParser#parse}   registerBeanDefinition
	 */
	void read06() {
	}

	/**
	 * 07. BeanFactory的准备工作
	 * <p>
	 * 初始化 beanFactory 工厂，对各种属性进行填充
	 * {@link AbstractApplicationContext#prepareBeanFactory}
	 * <p>
	 * {@link ComponentScanBeanDefinitionParser} 扫描注解注入类
	 * 1. Spel 表达式解析类
	 * {@link StandardBeanExpressionResolver}
	 * 2. 注册定制化解析器（扩展自定义属性编辑器 ）
	 * {@link PropertyEditorRegistrar#registerCustomEditors}
	 * 1. 自定义一个实现 PropertyEditorRegistrar 接口的编辑器
	 * 2. 自定义属性编辑器的注册器，实现 PropertyEditorRegistrar 接口
	 * 3. 让 Spring 能够识别到对应的注册器
	 * <p>
	 * 3. 扩展定义 MyAwareProcessor
	 * 其他忽略的 Aware（自定义的 Aware） 在 beanPostProcessor 中进行调用
	 * BeanNameAware,BeanClassLoaderAware,BeanFactoryAware 在 invokeAwareMethods 中进行调用
	 * {@link AbstractAutowireCapableBeanFactory#invokeAwareMethods(String, Object)}
	 * <p>
	 * Autowire
	 * {@link AbstractAutowireCapableBeanFactory#populateBean}
	 * 根据名称自动注入  autowireByName(beanName, mbd, bw, newPvs);
	 * 根据类型自动注入  autowireByType(beanName, mbd, bw, newPvs);
	 * 反射进行值处理时有两种方式
	 * 一：获取该属性对应的 set 方法进行赋值
	 * 二：获取到该属性对象 Field
	 */
	void red07() {
	}

	/**
	 * 8. BeanFactoryPostProcessor 的执行过程
	 * <p>
	 * 1. 获取到 beanFactory ,就可以对 工厂中所有属性进行操作
	 * 		postProcessBeanFactory 扩展
	 * 		{@link AbstractApplicationContext#postProcessBeanFactory(ConfigurableListableBeanFactory) }
	 * <ol>
	 *     <li>1）先执行 BeanDefinitionRegistry 类型的 beanFactory;先遍历实现 PriorityOrdered 接口的，然后是实现 Order 接口的，最后是无序的</li>
	 *    <li> 2) 然后执行不属于 BeanDefinitionRegistry 类型的，直接执行 postProcessBeanFactory 方法</li>
	 *    <li> 3）找到所有实现 BeanFactoryPostProcessor 接口的类</li>
	 * </ol>
	 * </p>
	 * <p>
	 * 2. BeanFactoryPostProcessor 与 BeanDefinitionRegistryPostProcessor 的区别
	 * {@link AbstractApplicationContext#invokeBeanFactoryPostProcessors(ConfigurableListableBeanFactory) }
	 * <ul>
	 *     <li> 后置处理器，增强器：对 beanFactory 中属性进行修改 {@link BeanFactoryPostProcessor（BFPP） } </li>
	 *     <li> BeanFactoryPostProcessor 子类， {@link BeanDefinitionRegistryPostProcessor } </li>
	 *     <li> 提供 BeanDefinition 的增删改查 {@link BeanDefinitionRegistry} </li>
	 *
	 *     <li> @Configuration、@Autowired、JSR-250 中的 @Resource 等注解 {@link AnnotationConfigUtils#registerAnnotationConfigProcessors(BeanDefinitionRegistry, Object)   } </li>
	 * </ul>
	 * </p>
	 * <p>
	 * 3. 自带的 BeanFactoryPostProcessor 子类
	 *
	 * <ul>
	 *       <li>  最重要的后置处理器 例：@Configuration 是后置处理器进行处理的 {@link  ConfigurationClassPostProcessor}
	 *       	打开 <context:component-scan></context:component-scan> 才会被扫描到
	 *       															{@link ComponentScanBeanDefinitionParser#registerComponents(XmlReaderContext, Set, Element)}
	 *       </li>
	 *       <li>  自定义自动注入 										{@link  CustomAutowireConfigurer} </li>
	 *       <li>  自定义编辑器 											{@link  CustomEditorConfigurer} </li>
	 *       <li>  @EventListener 支持 									{@link  EventListenerMethodProcessor} </li>
	 *       <li> 用于解析 bean 定义中属性值里面的占位符 					{@link PlaceholderConfigurerSupport } </li>
	 * </ul>
	 */
	void read08() {}


	/**
	 * 9. BeanFactoryPostProcessor 的执行过程
	 * <p>
	 *     1. 自定义 BeanDefinitionRegistryPostProcessor，分别实现 PriorityOrdered、Ordered 接口和不实现接口，BDRPP 被扫描执行的顺序不同
	 *     																{@link MyBeanDefinitionRegistryPostProcessor}
	 *     2. 每个阶段执行 BDRPP ，每次需要重新获取 BeanDefinitionRegistryPostProcessor
	 * </p>
	 */
	void read09() {}


}


