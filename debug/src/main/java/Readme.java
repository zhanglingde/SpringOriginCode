import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.support.BeanDefinitionReaderUtils;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.xml.BeanDefinitionParserDelegate;
import org.springframework.beans.factory.xml.DefaultBeanDefinitionDocumentReader;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.AbstractRefreshableApplicationContext;
import org.springframework.context.support.AbstractRefreshableConfigApplicationContext;

import com.ling.test02.MyClassPathXmlApplicationContext;
import org.springframework.context.support.AbstractXmlApplicationContext;
import org.springframework.core.io.Resource;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;
import org.springframework.beans.factory.xml.AbstractSingleBeanDefinitionParser;
import org.springframework.beans.factory.xml.AbstractBeanDefinitionParser;
import org.springframework.context.config.PropertyPlaceholderBeanDefinitionParser;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;



public class Readme {

	/**
	 * 1. 流程概述 {@link com.ling.test.Test}
	 * 2. 启动流程细节    {@link com.ling.test02.Test}
	 *	  spel 表达式（${username}）解析：配置文件，环境变量都是通过同样的方式进行解析的 <br>
	 *
	 *
	 *	  设置配置路径：
	 *	  {@link org.springframework.context.support.AbstractRefreshableConfigApplicationContext#setConfigLocations(String...)}
	 *
	 * refresh():
	 *  	{@link AbstractApplicationContext#refresh()}
	 * 1. 初始化属性值扩展,自定义环境变量，重写该方法
	 * 		{@link AbstractApplicationContext#initPropertySources}
	 * 		{@link MyClassPathXmlApplicationContext#initPropertySources()} (DefaultListableBeanFactory)}
	 * 2. 监听器初始化： this.earlyApplicationListeners  (SpringBoot 启动配合理解,SpringBoot 启动时会有很多监听器，放到 earlyApplicationListeners容器中)
	 * 3. 设置 bean 允许被覆盖，允许循环依赖，重写方法
	 * 		{@link AbstractRefreshableApplicationContext#customizeBeanFactory(DefaultListableBeanFactory)}
	 * 4. 初始化 documentReader，为 xml 文件解析做准备
	 * 		{@link AbstractRefreshableApplicationContext#loadBeanDefinitions}
	 */
	void readme01(){}

	/**
	 * 05. xml 配置文件加载过程 -> beanDefinition(一个标签解析成一个 BeanDefinition)
	 * 	1. 从网络加载配置文件，本地定义的文件 spring-bean/META-iNF  spring.schemas
	 *
	 *	2. 获取配置文件路径：
	 *				{@link AbstractXmlApplicationContext#loadBeanDefinitions(XmlBeanDefinitionReader)}
	 *  3. 读取xml 配置文件，生成document对象
	 *  			{@link XmlBeanDefinitionReader#doLoadBeanDefinitions(InputSource, Resource)}
	 *  4. 解析过程
	 *  			{@link DefaultBeanDefinitionDocumentReader#doRegisterBeanDefinitions(Element)}
	 *  		解析 bean标签 {@link DefaultBeanDefinitionDocumentReader#processBeanDefinition}
	 *  5. 将xml解析成 beanDefinition对象后，将 BeanDefinition 对象放入 BeanFactory
	 *    		beanDefinitionMap<beanName,BeanDefinition>
	 *    		beanDefinitionNames<beanName>
	 *    	   	{@link BeanDefinitionReaderUtils#registerBeanDefinition(BeanDefinitionHolder, BeanDefinitionRegistry)}
	 *
	 */
	void readme05(){}

	/**
	 * 06. 自定义标签加载过程
	 *
	 * XmlReaderContext -> namespaceHandlerResolver
	 * 相关对象：
	 * 		{@link PropertyPlaceholderBeanDefinitionParser}
	 * 		{@link PropertySourcesPlaceholderConfigurer}
	 * 非默认命名空间加载过程：
	 * 		1. 加载 spring.handlers 配置文件
	 * 		2. 将配置文件内容加载到 handlerMappings 集合中
	 * 		3. 根据指定的 key 获取对应的处理器（一个处理器处理一种标签）
	 * 自定义标签解析步骤：
	 * 		1. 创建一个对应的解析器处理类（在 init 方法中添加 parser 类）
	 * 		2. 创建一个普通的 spring.handlers 配置文件，让应用程序能够完成加载工作
	 * 		3. 创建对应标签的 parser 类（对当前标签的其他属性值进行解析工作）
	 * context 标签解析：
	 * 		1. createReaderContext(resource)：创建ReaderContext，读取 spring.handlers 命名空间的Handler
	 * 			{@link XmlBeanDefinitionReader#registerBeanDefinitions}
	 * 	    2. 解析导入的命名空间 <context><aop><tx> 等
	 * 	    	{@link DefaultBeanDefinitionDocumentReader#parseBeanDefinitions}
	 * 	    	{@link BeanDefinitionParserDelegate#parseCustomElement(Element, BeanDefinition)}
	 * 	    获取命名空间 url -> 根据命名空间找到对应的 Handler（spring.handlers）-> ContextNamespaceHandler（初始化解析器）->
	 * 	    通过反射将 Handler 实例化存在 handlerMappings 中 -> Handler init 方法为每个属性值创建不同解析器（）
	 * 	    	<ling:user> 标签需要一个 UserBeanDefinitionParser 的解析器
	 * 	        <ling:book> 标签需要一个 BookBeanDefinitionParser 的解析器
	 * 	   3. 解析：将标签属性值转换成对象（放入 beanDefinition）
	 * 	   		{@link AbstractSingleBeanDefinitionParser#parseInternal}中的 doParser 方法进行解析
	 * 	   4. 将解析后的对象注册成 beanDefinition
	 * 	   		{@link AbstractBeanDefinitionParser#parse}   registerBeanDefinition
	 */
	void read06(){}
}


