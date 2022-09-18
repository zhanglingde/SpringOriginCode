import org.springframework.web.context.ConfigurableWebApplicationContext;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.*;
import org.springframework.web.servlet.handler.SimpleUrlHandlerMapping;
import org.springframework.web.servlet.mvc.SimpleControllerHandlerAdapter;
import org.springframework.web.servlet.handler.AbstractHandlerMapping;
import org.springframework.web.servlet.handler.AbstractUrlHandlerMapping;
import org.springframework.web.servlet.handler.AbstractDetectingUrlHandlerMapping;
import org.springframework.web.servlet.handler.BeanNameUrlHandlerMapping;
import org.springframework.web.servlet.handler.AbstractHandlerMethodMapping;
import org.springframework.web.servlet.mvc.condition.CompositeRequestCondition;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServlet;

import com.ling.test02.controller.RedirectParamController;

import java.lang.reflect.Method;
import java.util.List;

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
     * Spring MVC 处理请求
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
     * Spring MVC 九大组件之 HandleMapping
     *
     * <li> Spring 自己 HandlerMapping {@link SimpleUrlHandlerMapping}</li>
     *
     * <ol>
     *     <li>AbstractHandlerMapping</li>
     *     <ul>
     *          <li> {@link AbstractHandlerMapping#initApplicationContext()}</li>
     *          <li> {@link AbstractHandlerMapping#getHandler(HttpServletRequest)} ()}</li>
     *     </ul>
     *     <li> AbstractUrlHandlerMapping {@link AbstractUrlHandlerMapping AbstractUrlHandlerMapping }</li>
     *     <ul>
     *         <li> 获取 Handler 的实现  {@link AbstractUrlHandlerMapping#getHandlerInternal(HttpServletRequest)} </li>
     *         <li> 使用 lookupPath 从 Map 中查找 Handler {@link AbstractUrlHandlerMapping#lookupHandler(String, HttpServletRequest) }</li>
     *         <li> 注册 Handler {@link AbstractUrlHandlerMapping#registerHandler(String[], String)}</li>
     *     </ul>
     *     <li> 子类 SimpleUrlHandlerMapping：将配置的内容（urlMap）注册到 AbstractUrlHandlerMapping {@link SimpleUrlHandlerMapping SimpleUrlHandlerMapping}</li>
     *     <li> 子类 AbstractDetectingUrlHandlerMapping {@link AbstractDetectingUrlHandlerMapping}</li>
     *     <ul>
     *         <li> BeanNameUrlHandlerMapping {@link BeanNameUrlHandlerMapping}</li>
     *         <li> AbstractControllerUrlHandlerMapping(已移除) </li>
     *     </ul>
     *     <li> AbstractHandlerMethodMapping 将 Method 作为 Handler 来使用，如 @RequestMapping 注释的方法（现在使用最多的一种 Handler）{@link AbstractHandlerMethodMapping}</li>
     *     <ul>
     *         <li> 保存了 7 个 RequestCondition，所以可以在 @RequestMapping 中给处理器指定多种匹配方式 {@link RequestMappingInfo} </li>
     *         <li> 该类实现了 InitializingBean接口，所以 Spring 容器会自动调用其 afterPropertiesSet 方法，后又交给 initHandlerMethods 方法完成具体的初始化 {@link AbstractHandlerMethodMapping#afterPropertiesSet()}</li>
     *         <li> 将 Handler 保存到 map 里 {@link AbstractHandlerMethodMapping#detectHandlerMethods(Object)}</li>
     *         <li> 获取 Method 的匹配条件 {@link AbstractHandlerMethodMapping#getMappingForMethod(Method, Class) getMappingForMethod} </li>
     *         <li> 将找到的 HandlerMethod 注册到三个 Map 里 {@link AbstractHandlerMethodMapping#registerHandlerMethod(Object, Method, Object) registerHandlerMethod } </li>
     *
     *         
     *     </ul>
     * </ol>
     */
    void read03(){}

    /**
     * Spring 自己 HandlerAdapter {@link SimpleControllerHandlerAdapter}
     */
    void read04(){}

    /**
     * 设计模式
     *
     * <ol>
     *     <li> 模板方法：{@link FrameworkServlet#doService(HttpServletRequest, HttpServletResponse)} </li>
     *     <li> 模板方法：{@link AbstractHandlerMapping#extendInterceptors(List)}</li>
     *     <li> 模板方法：{@link AbstractHandlerMapping#getHandlerInternal(HttpServletRequest)}</li>
     *     <li> 模板方法：{@link AbstractUrlHandlerMapping#validateHandler(Object, HttpServletRequest) }</li>
     *     <li> {@link AbstractDetectingUrlHandlerMapping#determineUrlsForHandler}</li>
     *     <li> 模板方法：根据一定的规则筛选出 Handler {@link AbstractHandlerMethodMapping#isHandler(Class)} </li>
     *     <li> 模板方法具体实现在 RequestMappingHandlerMapping {@link AbstractHandlerMethodMapping#getMappingForMethod(Method, Class)}  } </li>
     *
     *     <li> 装饰者模式：{@link org.springframework.context.i18n.LocaleContextHolder}</li>
     *     <li> 责任链模式：可以封装多个别的 RequestCondition 封装到自己的一个变量里 {@link CompositeRequestCondition}</li>
     * </ol>
     */
    void read0410(){}
}
