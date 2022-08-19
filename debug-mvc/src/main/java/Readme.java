import org.springframework.web.context.ConfigurableWebApplicationContext;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.FrameworkServlet;
import org.springframework.web.servlet.HttpServletBean;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;

public class Readme {

    /**
     * Servlet 初始化的时候加载配置文件 {@link org.springframework.web.context.ContextLoaderListener#contextInitialized(ServletContextEvent)}
     *
     * <ol>
     *     <li> 初始化容器,未配置默认加载 XmlWebApplicationContext  {@link org.springframework.web.context.ContextLoader#determineContextClass(ServletContext)} </li>
     *     <li> 设置父容器 {@link org.springframework.web.context.ContextLoader#loadParentContext(ServletContext)}</li>
     *     <li> 加载 Spring 配置文件，创建 Spring 容器：{@link org.springframework.web.context.ContextLoader#configureAndRefreshWebApplicationContext(ConfigurableWebApplicationContext, ServletContext)}</li>
     *     <li> 加载 web.xml ,启动 Servlet，创建 Spring Mvc 容器 {@link HttpServletBean#init()}</li>
     *     <li> BeanWrapper 的作用,方便设置属性值 {@link com.ling.test01.utils.BeanWrapperTest}</li>
     *     <li> {@link FrameworkServlet#initServletBean()}</li>
     *     <li> {@link FrameworkServlet#initWebApplicationContext()} </li>
     *
     *
     *     <li> 创建 SpringMvc 的容器： {@link FrameworkServlet#createWebApplicationContext(WebApplicationContext)} </li>
     *     <li> 配置和初始化 wac： {@link FrameworkServlet#configureAndRefreshWebApplicationContext(ConfigurableWebApplicationContext)}  </li>
     *
     *
     *
     *
     *
     *
     *
     * </ol>
     *
     */
    void read01(){}
}
