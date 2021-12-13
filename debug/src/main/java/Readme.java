import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.AbstractRefreshableApplicationContext;
import org.springframework.context.support.AbstractRefreshableConfigApplicationContext;
import com.ling.test02.MyClassPathXmlApplicationContext;

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
public class Readme {
}
