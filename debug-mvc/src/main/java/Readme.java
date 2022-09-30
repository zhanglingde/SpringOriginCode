import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.ConfigurableWebApplicationContext;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.method.HandlerMethod;
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
import org.springframework.web.servlet.mvc.HttpRequestHandlerAdapter;
import org.springframework.web.servlet.handler.SimpleServletHandlerAdapter;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;
import org.springframework.web.servlet.view.AbstractCachingViewResolver;
import org.springframework.web.servlet.view.ViewResolverComposite;
import org.springframework.web.servlet.view.BeanNameViewResolver;
import org.springframework.web.servlet.view.ContentNegotiatingViewResolver;
import org.springframework.web.servlet.view.ResourceBundleViewResolver;
import org.springframework.web.servlet.view.XmlViewResolver;
import org.springframework.web.servlet.view.UrlBasedViewResolver;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.web.servlet.view.ViewResolverComposite;
import org.springframework.web.servlet.theme.AbstractThemeResolver;
import org.springframework.web.servlet.theme.FixedThemeResolver;
import org.springframework.web.servlet.theme.SessionThemeResolver;
import org.springframework.web.servlet.theme.CookieThemeResolver;
import org.springframework.web.servlet.support.RequestContext;
import org.springframework.web.servlet.support.RequestContextUtils;
import org.springframework.web.multipart.support.StandardServletMultipartResolver;
import org.springframework.web.multipart.support.StandardMultipartHttpServletRequest;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import org.springframework.web.multipart.commons.CommonsFileUploadSupport;
import org.springframework.web.servlet.view.InternalResourceViewResolver;
import org.springframework.web.servlet.view.freemarker.FreeMarkerViewResolver;
import org.springframework.web.servlet.i18n.*;

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
import java.util.Locale;

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
     * 处理器适配器  HandlerAdapter
     * <ol>
     *     <li> 适配 HttpRequestHandler {@link HttpRequestHandlerAdapter} </li>
     *     <li> 适配 Servlet {@link SimpleServletHandlerAdapter} </li>
     *     <li> 适配 Controller 类型的 Handler {@link SimpleControllerHandlerAdapter} </li>
     *
     *     <li> {@link RequestMappingHandlerAdapter#handleInternal(HttpServletRequest, HttpServletResponse, HandlerMethod) RequestMappingHandlerAdapter} </li>
     *     相关组件
     *     <ol>
     *         <li> @SessionAttribute 使用{@link com.ling.test04.BookController}</li>
     *         <ul>
     *              <li> 用于创建 WebDataBinder，WebDataBinder 用于参数绑定，主要功能就是实现参数和 String 之间的转换 {@link RequestMappingHandlerAdapter#getDataBinderFactory(HandlerMethod)}</li>
     *              <li> 两个功能 1. 在处理器具体处理之前对 Model 进行初始化 2. 处理完请求后对 Model 参数进行更新 {@link RequestMappingHandlerAdapter#getModelFactory(HandlerMethod, WebDataBinderFactory)}</li>
     *              <li> 继承 HandlerMethod，可以直接执行；实际请求的处理就是通过该类执行的，参数绑定、处理请求以及返回值处理都在该类中完成 {@link RequestMappingHandlerAdapter#createInvocableHandlerMethod(HandlerMethod)}</li>
     *         </ul>
     *         <li> ModelAndViewContainer:  </li>
     *     </ol>
     * </ol>
     */
    void read04(){}

    /**
     * 请求异常处理 HandlerExceptionResolver
     * <ol>
     *     <li> 异常解析的父类，定义了通用的解析流程（模板方法） {@link org.springframework.web.servlet.handler.AbstractHandlerExceptionResolver} </li>
     *     <li> 对 HandlerMethod 进行适配 {@link org.springframework.web.servlet.handler.AbstractHandlerMethodExceptionResolver#shouldApplyTo(HttpServletRequest, Object)} </li>
     *     <li> 进行异常解析 {@link org.springframework.web.servlet.mvc.method.annotation.ExceptionHandlerExceptionResolver#doResolveHandlerMethodException(HttpServletRequest, HttpServletResponse, HandlerMethod, Exception)} </li>
     *     <li> 根据异常类型的不同，使用不同的方法进行处理 {@link org.springframework.web.servlet.mvc.support.DefaultHandlerExceptionResolver} </li>
     *     <li> 用来解析注释了 @ResponseStatus 的异常{@link org.springframework.web.servlet.mvc.annotation.ResponseStatusExceptionResolver} </li>
     *     <li> 需要提前配置异常类和 view 的对应关系{@link org.springframework.web.servlet.handler.SimpleMappingExceptionResolver} </li>
     * </ol>
     */
    void read05(){}

    /**
     * 视图解析器 ViewResolver
     *
     * <ol>
     *     <li> 根据 ViewName 从 ApplicationContext 容器中查找相应的 bean 做 View 的 {@link BeanNameViewResolver} </li>
     *     <li> 可以存放多个 ViewResolver {@link ViewResolverComposite} </li>
     *     <li> 增加了对 MediaType 和后缀的支持 {@link ContentNegotiatingViewResolver#initServletContext(ServletContext)} </li>
     *     <li> 提供了缓存视图的功能 {@link AbstractCachingViewResolver} </li>
     *     <ul>
     *         <li> 根据 properties 配置文件来解析视图 {@link ResourceBundleViewResolver} </li>
     *         <li> 根据 xml 配置文件来解析视图 {@link XmlViewResolver} </li>
     *         <li> {@link UrlBasedViewResolver} </li>
     *         <ol>
     *             <li> {@link UrlBasedViewResolver#createView(String, Locale)} </li>
     *             <li> 调用父类创建 view {@link UrlBasedViewResolver#loadView(String, Locale)}</li>
     *             <li> 调用父类创建 view {@link UrlBasedViewResolver#buildView(String)}</li>
     *         </ol>
     *         <li> {@link InternalResourceViewResolver} </li>
     *         <li> {@link FreeMarkerViewResolver} </li>
     *     </ul>
     * </ol>
     */
    void read06(){}

    /**
     * LocaleResolver
     *
     * <ol>
     *     <li> {@link AbstractLocaleResolver }</li>
     *     <li> 使用 Header 里的 acceptlanguage,不可以在程序中修改 {@link AcceptHeaderLocaleResolver} </li>
     *     <li> 解析出固定的 Locale,创建时就确定好 Locale,之后无法修改 {@link FixedLocaleResolver} </li>
     *     <li> 将 Locale 保存到 Session 中 {@link SessionLocaleResolver} </li>
     *     <li> 将 Locale 保存到 Cookie 中 {@link CookieLocaleResolver} </li>
     *     <li> 从Spring MVC4.0开始，LocaleResolver添加了一个子接口LocaleContextResolver，其中增加了获取和设置LocaleContext的能力，并添加了抽象类AbstractLocaleContextResolver，抽象类添加了对TimeZone也就是时区的支持 {@link LocaleContextResolver} </li>
     * </ol>
     */
    void read07(){}

    /**
     * 主题解析器 ThemeResolver
     *
     * <ul>
     *     <li> {@link ThemeResolver} </li>
     *     <li> 设置了默认主题名，默认值为 theme {@link AbstractThemeResolver} </li>
     *     <ul>
     *         <li> 用于解析固定的主题名，主题名的创建时设置，不能修改 {@link FixedThemeResolver} </li>
     *         <li> 将主题保存在 Session 中，可以修改 {@link SessionThemeResolver} </li>
     *     </ul>
     *     <li> 将主题保存在 Cookie 中，继承了 CookieGenerator 方便处理 Cookie，所以就不能继承 AbstractThemeResolver {@link CookieThemeResolver} </li>
     *
     *     <li> {@link RequestContext#getThemeMessage(String, Object[], String)} </li>
     *     <li> SpringMVC 默认容器是 XmlWebApplicationContext，父类是 AbstractRefreshableWebApplicationContext，这个类实现了
     *     ThemeSource 接口，内部封装了一个 ThemeSource 属性 {@link RequestContextUtils#getTheme(HttpServletRequest)} </li>
     *
     *
     * </ul>
     */
    void read08(){}

    /**
     * 文件上传解析器 MultipartResolver
     *
     * <ul>
     *     <li> 使用 Servlet3.0 标准的上传方式，只需要调用 request 的 getParts 方法就可以获取所有上传的文件 {@link StandardServletMultipartResolver} </li>
     *     <ul>
     *         <li> {@link StandardMultipartHttpServletRequest#parseRequest(HttpServletRequest)} </li>
     *     </ul>
     *     <li> Apache 的 commons-fileupload 来完成具体的上传操作 {@link CommonsMultipartResolver} </li>
     *     <ul>
     *         <li> {@link CommonsMultipartResolver#resolveMultipart(HttpServletRequest)}</li>
     *         <li> {@link CommonsFileUploadSupport#parseFileItems(List, String)} </li>
     *     </ul>
     * </ul>
     */
    void read09(){}

    /**
     * <ol>
     *     <li> retrieveFlashMaps、getFlashMapsMutex和updateFlashMaps方法都在子类SessionFlashMapManager中实现 {@link org.springframework.web.servlet.support.AbstractFlashMapManager#saveOutputFlashMap(FlashMap, HttpServletRequest, HttpServletResponse)} </li>
     *     <li> {@link org.springframework.web.servlet.support.SessionFlashMapManager}</li>
     * </ol>
     */
    void read10(){}

    /**
     * RequestToViewNameTranslator：在 view 为空时使用它根据 reqeust 获取 viewName
     *
     * <ol>
     *     <li> {@link org.springframework.web.servlet.view.DefaultRequestToViewNameTranslator#getViewName(HttpServletRequest)} </li>
     * </ol>
     */
    void read12(){}

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
     *     <li> 子类解析视图的如入口方法 {@link AbstractCachingViewResolver#loadView(String, Locale)}</li>
     *     <li> 异常解析的父类，定义了通用的解析流程（模板方法） {@link org.springframework.web.servlet.handler.AbstractHandlerExceptionResolver} </li>
     *     <li> {@link org.springframework.web.servlet.support.AbstractFlashMapManager#getFlashMapsMutex(HttpServletRequest)} </li>
     *     <li> 模板方法获取 List<FlashMap> {@link org.springframework.web.servlet.support.AbstractFlashMapManager#retrieveFlashMaps(HttpServletRequest)} </li>
     *
     *     <li> 装饰者模式：{@link LocaleContextHolder}</li>
     *
     *     <li> 责任链模式：可以封装多个别的 RequestCondition 封装到自己的一个变量里（XXXComposite CompositeXXX 都是） {@link CompositeRequestCondition}</li>
     *     <li> 视图解析器 {@link ViewResolverComposite} </li>
     * </ol>
     */
    void read0410(){}
}
