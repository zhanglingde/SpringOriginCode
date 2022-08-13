import org.springframework.web.context.ConfigurableWebApplicationContext;
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
     *
     *
     *
     * </ol>
     *
     */
    void read01(){}
}
