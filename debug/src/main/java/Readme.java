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
import org.springframework.beans.factory.xml.XmlBeanDefinitionReade;

/**
 * 1. 流程概述 {@link com.ling.test.Test}
 * 2. 启动流程细节    {@link com.ling.test02.Test}
 *	  spel 表达式（${username}）解析：配置文件，环境变量都是通过同样的方式进行解析的 <br>
 *
 *
 *	  设置配置路径：
 *	  {@link AbstractRefreshableConfigApplicationContext#setConfigLocations(String...)}
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

/**
 * 5. xml 配置文件加载过程 -> beanDefinition
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
public class Readme {
}
