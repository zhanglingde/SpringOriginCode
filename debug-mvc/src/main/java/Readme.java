import org.springframework.web.context.ConfigurableWebApplicationContext;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.*;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServlet;

import com.ling.test02.controller.RedirectParamController;

public class Readme {

    /**
     * Servlet 初始化的时候加载配置文件 {@link org.springframework.web.context.ContextLoaderListener#contextInitialized(ServletContextEvent)}
     *
     * <ol>
     *     <li> 加载 web.xml 文件 </li>
     *     <li> 初始化容器,未配置默认加载 XmlWebApplicationContext  {@link org.springframework.web.context.ContextLoader#determineContextClass(ServletContext)} </li>
     *     <li> 设置父容器 {@link org.springframework.web.context.ContextLoader#loadParentContext(ServletContext)}</li>
     *     <li> 加载 Spring 配置文件，创建 Spring 容器：{@link org.springframework.web.context.ContextLoader#configureAndRefreshWebApplicationContext(ConfigurableWebApplicationContext, ServletContext)}</li>
     *
     *     <li> HttpServletBean：加载 web.xml ,启动 Servlet，创建 Spring Mvc 容器 {@link HttpServletBean#init()}</li>
     *     <li> BeanWrapper 是 Spring 提供的一个用来操作 JavaBean 属性的工具,可以直接修改一个对象的属性 {@link com.ling.test01.utils.BeanWrapperTest}</li>
     *
     *     <li> FrameworkServlet：初始化 DispatchServlet {@link FrameworkServlet#initServletBean()}</li>
     *     <li> {@link FrameworkServlet#initWebApplicationContext()} </li>
     *     <ol>
     *         <li> 获取 Spring 的根容器 rootContext </li>
     *         <li> 设置 webApplicationContext 并根据情况调用 onRefresh 方法（有三种方法） </li>
     *         <li> 将 webApplicationContext 设置到 ServletContext 中 </li>
     *     </ol>
     *
     *
     *     <li> 创建 SpringMvc 的容器： {@link FrameworkServlet#createWebApplicationContext(WebApplicationContext)} </li>
     *     <li> refresh 配置和初始化 wac(SpringMvc 容器)： {@link FrameworkServlet#configureAndRefreshWebApplicationContext(ConfigurableWebApplicationContext)}  </li>
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


    /**
     * Spring 请求处理方式：
     * <ol>
     *     <li> Servlet 处理过程：{@link HttpServlet#service(HttpServletRequest, HttpServletResponse) HttpServlet#service}</li>
     *     <li> FrameworkServlet 重写了除 doHead 外的所有请求处理方式：{@link FrameworkServlet#service(HttpServletRequest, HttpServletResponse) FrameworkServlet#service}</li>
     *     <li> FrameworkServlet 处理请求最核心的方法：{@link FrameworkServlet#processRequest(HttpServletRequest, HttpServletResponse) processRequest}</li>
     *     <li> DispatchServlet 执行请求的入口{@link DispatcherServlet#doService(HttpServletRequest, HttpServletResponse) doService} ：</li>
     *     <ol>
     *         <li> 对 request 设置一些属性，如果是 include 请求会对 request 当前的属性做快照备份，在处理结束后恢复</li>
     *         <li> 重定向参数传递 {@link RedirectParamController}</li>
     *     </ol>
     *     <li> doService 会将请求转发到 doDispatch {@link DispatcherServlet#doDispatch(HttpServletRequest, HttpServletResponse) doDispatch} </li>
     *     <ol>
     *         <li>  根据 request 找到 Handler </li>
     *         <li>  根据 Handler 找到对应的 HandlerAdapter </li>
     *         <li>  用 HandlerAdapter 处理 Handler </li>
     *         <li>  调用 processDispatchResult 方法处理上面处理之后的结果（包含找到 View 并渲染输出给用户） {@link DispatcherServlet#processDispatchResult(HttpServletRequest, HttpServletResponse, HandlerExecutionChain, ModelAndView, Exception) processDispatchResult}</li>
     *
     *     </ol>
     * </ol>
     */
    void read02(){}

    /**
     * Spring MVC 九大组件
     *
     * <ol>
     *     <li> HandlerMapping {@link }</li>
     * </ol>
     */
    void read03(){}

    /**
     * 设计模式
     *
     * <ol>
     *     <li> 模板方法：{@link FrameworkServlet#doService(HttpServletRequest, HttpServletResponse)} </li>
     *     <li> 装饰者模式：{@link org.springframework.context.i18n.LocaleContextHolder}</li>
     * </ol>
     */
    void read04(){}
}
