/*
 * Copyright 2002-2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.web.servlet.view;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.AnnotationAwareOrderComparator;
import org.springframework.http.MediaType;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.accept.ContentNegotiationManager;
import org.springframework.web.accept.ContentNegotiationManagerFactoryBean;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.support.WebApplicationObjectSupport;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.SmartView;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.ViewResolver;

/**
 * Implementation of {@link ViewResolver} that resolves a view based on the request file name
 * or {@code Accept} header.
 *
 * <p>The {@code ContentNegotiatingViewResolver} does not resolve views itself, but delegates to
 * other {@link ViewResolver ViewResolvers}. By default, these other view resolvers are picked up automatically
 * from the application context, though they can also be set explicitly by using the
 * {@link #setViewResolvers viewResolvers} property. <strong>Note</strong> that in order for this
 * view resolver to work properly, the {@link #setOrder order} property needs to be set to a higher
 * precedence than the others (the default is {@link Ordered#HIGHEST_PRECEDENCE}).
 *
 * <p>This view resolver uses the requested {@linkplain MediaType media type} to select a suitable
 * {@link View} for a request. The requested media type is determined through the configured
 * {@link ContentNegotiationManager}. Once the requested media type has been determined, this resolver
 * queries each delegate view resolver for a {@link View} and determines if the requested media type
 * is {@linkplain MediaType#includes(MediaType) compatible} with the view's
 * {@linkplain View#getContentType() content type}). The most compatible view is returned.
 *
 * <p>Additionally, this view resolver exposes the {@link #setDefaultViews(List) defaultViews} property,
 * allowing you to override the views provided by the view resolvers. Note that these default views are
 * offered as candidates, and still need have the content type requested (via file extension, parameter,
 * or {@code Accept} header, described above).
 *
 * <p>For example, if the request path is {@code /view.html}, this view resolver will look for a view
 * that has the {@code text/html} content type (based on the {@code html} file extension). A request
 * for {@code /view} with a {@code text/html} request {@code Accept} header has the same result.
 *
 * @author Arjen Poutsma
 * @author Juergen Hoeller
 * @author Rossen Stoyanchev
 * @since 3.0
 * @see ViewResolver
 * @see InternalResourceViewResolver
 * @see BeanNameViewResolver
 */
public class ContentNegotiatingViewResolver extends WebApplicationObjectSupport
		implements ViewResolver, Ordered, InitializingBean {

	@Nullable
	private ContentNegotiationManager contentNegotiationManager;

	private final ContentNegotiationManagerFactoryBean cnmFactoryBean = new ContentNegotiationManagerFactoryBean();

	private boolean useNotAcceptableStatusCode = false;

	@Nullable
	private List<View> defaultViews;

	@Nullable
	private List<ViewResolver> viewResolvers;

	private int order = Ordered.HIGHEST_PRECEDENCE;


	/**
	 * Set the {@link ContentNegotiationManager} to use to determine requested media types.
	 * <p>If not set, ContentNegotiationManager's default constructor will be used,
	 * applying a {@link org.springframework.web.accept.HeaderContentNegotiationStrategy}.
	 * @see ContentNegotiationManager#ContentNegotiationManager()
	 */
	public void setContentNegotiationManager(@Nullable ContentNegotiationManager contentNegotiationManager) {
		this.contentNegotiationManager = contentNegotiationManager;
	}

	/**
	 * Return the {@link ContentNegotiationManager} to use to determine requested media types.
	 * @since 4.1.9
	 */
	@Nullable
	public ContentNegotiationManager getContentNegotiationManager() {
		return this.contentNegotiationManager;
	}

	/**
	 * Indicate whether a {@link HttpServletResponse#SC_NOT_ACCEPTABLE 406 Not Acceptable}
	 * status code should be returned if no suitable view can be found.
	 * <p>Default is {@code false}, meaning that this view resolver returns {@code null} for
	 * {@link #resolveViewName(String, Locale)} when an acceptable view cannot be found.
	 * This will allow for view resolvers chaining. When this property is set to {@code true},
	 * {@link #resolveViewName(String, Locale)} will respond with a view that sets the
	 * response status to {@code 406 Not Acceptable} instead.
	 */
	public void setUseNotAcceptableStatusCode(boolean useNotAcceptableStatusCode) {
		this.useNotAcceptableStatusCode = useNotAcceptableStatusCode;
	}

	/**
	 * Whether to return HTTP Status 406 if no suitable is found.
	 */
	public boolean isUseNotAcceptableStatusCode() {
		return this.useNotAcceptableStatusCode;
	}

	/**
	 * Set the default views to use when a more specific view can not be obtained
	 * from the {@link ViewResolver} chain.
	 */
	public void setDefaultViews(List<View> defaultViews) {
		this.defaultViews = defaultViews;
	}

	public List<View> getDefaultViews() {
		return (this.defaultViews != null ? Collections.unmodifiableList(this.defaultViews) :
				Collections.emptyList());
	}

	/**
	 * Sets the view resolvers to be wrapped by this view resolver.
	 * <p>If this property is not set, view resolvers will be detected automatically.
	 */
	public void setViewResolvers(List<ViewResolver> viewResolvers) {
		this.viewResolvers = viewResolvers;
	}

	public List<ViewResolver> getViewResolvers() {
		return (this.viewResolvers != null ? Collections.unmodifiableList(this.viewResolvers) :
				Collections.emptyList());
	}

	public void setOrder(int order) {
		this.order = order;
	}

	@Override
	public int getOrder() {
		return this.order;
	}


    /**
     * 两种方式初始化 viewResolvers
     *
     * 1. 手动设置（手动设置但不在 Spring 容器中，会对它进行初始化）
     * 2. 若没有设置则自动获取 Spring 容器中除了它自己外的所有 ViewResolver 并设置到 viewResolvers 中
     *
     */
	@Override
	protected void initServletContext(ServletContext servletContext) {
		// 获取容器中所有 ViewResolver 类型的 bean，从整个 Spring 容器中获取而不只是 SpringMVC 容器中获取
		Collection<ViewResolver> matchingBeans =
				BeanFactoryUtils.beansOfTypeIncludingAncestors(obtainApplicationContext(), ViewResolver.class).values();
		// 如果没有手动注册则将容器中找到的 ViewResolver 设置到 viewResolvers 属性中
		if (this.viewResolvers == null) {
			this.viewResolvers = new ArrayList<>(matchingBeans.size());
			for (ViewResolver viewResolver : matchingBeans) {
				if (this != viewResolver) {
					this.viewResolvers.add(viewResolver);
				}
			}
		}
		else {
			// 如果是手动注册的，并且在容器中不存在，则进行初始化
			for (int i = 0; i < this.viewResolvers.size(); i++) {
				ViewResolver vr = this.viewResolvers.get(i);
				if (matchingBeans.contains(vr)) {
					continue;
				}
				String name = vr.getClass().getName() + i;
				obtainApplicationContext().getAutowireCapableBeanFactory().initializeBean(vr, name);
			}

		}
		// 按照 Order 属性进行排序
		AnnotationAwareOrderComparator.sort(this.viewResolvers);
		this.cnmFactoryBean.setServletContext(servletContext);
	}

	@Override
	public void afterPropertiesSet() {
		if (this.contentNegotiationManager == null) {
			this.contentNegotiationManager = this.cnmFactoryBean.build();
		}
		if (this.viewResolvers == null || this.viewResolvers.isEmpty()) {
			logger.warn("No ViewResolvers configured");
		}
	}


	@Override
	@Nullable
	public View resolveViewName(String viewName, Locale locale) throws Exception {
		// 从 Holder 中获取 RequestAttributes，进而在下面获取 request
		RequestAttributes attrs = RequestContextHolder.getRequestAttributes();
		Assert.state(attrs instanceof ServletRequestAttributes, "No current ServletRequestAttributes");
		// 使用 request 获取 MediaType,用作需要满足的条件
		List<MediaType> requestedMediaTypes = getMediaTypes(((ServletRequestAttributes) attrs).getRequest());
		if (requestedMediaTypes != null) {
			// 获取所有候选视图，内部通过遍历封装的 viewResolvers 来解析
			List<View> candidateViews = getCandidateViews(viewName, locale, requestedMediaTypes);
			// 从多个视图中找出最优视图
			View bestView = getBestView(candidateViews, requestedMediaTypes, attrs);
			if (bestView != null) {
				return bestView;
			}
		}

		String mediaTypeInfo = logger.isDebugEnabled() && requestedMediaTypes != null ?
				" given " + requestedMediaTypes.toString() : "";

		if (this.useNotAcceptableStatusCode) {
			if (logger.isDebugEnabled()) {
				logger.debug("Using 406 NOT_ACCEPTABLE" + mediaTypeInfo);
			}
			return NOT_ACCEPTABLE_VIEW;
		}
		else {
			logger.debug("View remains unresolved" + mediaTypeInfo);
			return null;
		}
	}

	/**
	 * Determines the list of {@link MediaType} for the given {@link HttpServletRequest}.
	 * @param request the current servlet request
	 * @return the list of media types requested, if any
	 */
	@Nullable
	protected List<MediaType> getMediaTypes(HttpServletRequest request) {
		Assert.state(this.contentNegotiationManager != null, "No ContentNegotiationManager set");
		try {
			ServletWebRequest webRequest = new ServletWebRequest(request);
			List<MediaType> acceptableMediaTypes = this.contentNegotiationManager.resolveMediaTypes(webRequest);
			List<MediaType> producibleMediaTypes = getProducibleMediaTypes(request);
			Set<MediaType> compatibleMediaTypes = new LinkedHashSet<>();
			for (MediaType acceptable : acceptableMediaTypes) {
				for (MediaType producible : producibleMediaTypes) {
					if (acceptable.isCompatibleWith(producible)) {
						compatibleMediaTypes.add(getMostSpecificMediaType(acceptable, producible));
					}
				}
			}
			List<MediaType> selectedMediaTypes = new ArrayList<>(compatibleMediaTypes);
			MediaType.sortBySpecificityAndQuality(selectedMediaTypes);
			return selectedMediaTypes;
		}
		catch (HttpMediaTypeNotAcceptableException ex) {
			if (logger.isDebugEnabled()) {
				logger.debug(ex.getMessage());
			}
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	private List<MediaType> getProducibleMediaTypes(HttpServletRequest request) {
		Set<MediaType> mediaTypes = (Set<MediaType>)
				request.getAttribute(HandlerMapping.PRODUCIBLE_MEDIA_TYPES_ATTRIBUTE);
		if (!CollectionUtils.isEmpty(mediaTypes)) {
			return new ArrayList<>(mediaTypes);
		}
		else {
			return Collections.singletonList(MediaType.ALL);
		}
	}

	/**
	 * Return the more specific of the acceptable and the producible media types
	 * with the q-value of the former.
	 */
	private MediaType getMostSpecificMediaType(MediaType acceptType, MediaType produceType) {
		produceType = produceType.copyQualityValue(acceptType);
		return (MediaType.SPECIFICITY_COMPARATOR.compare(acceptType, produceType) < 0 ? acceptType : produceType);
	}

	private List<View> getCandidateViews(String viewName, Locale locale, List<MediaType> requestedMediaTypes)
			throws Exception {

		List<View> candidateViews = new ArrayList<>();
		if (this.viewResolvers != null) {
			Assert.state(this.contentNegotiationManager != null, "No ContentNegotiationManager set");
			// 遍历 viewResolvers 进行视图解析，并将解析出的视图添加到候选视图 candidateViews
			for (ViewResolver viewResolver : this.viewResolvers) {
				// 直接使用逻辑视图进行解析
				View view = viewResolver.resolveViewName(viewName, locale);
				if (view != null) {
					candidateViews.add(view);
				}
				// 遍历 requestedMediaTypes 获取到对应的后缀，然后添加到逻辑视图后面作为一个新的视图名进行解析
				for (MediaType requestedMediaType : requestedMediaTypes) {
					List<String> extensions = this.contentNegotiationManager.resolveFileExtensions(requestedMediaType);
					for (String extension : extensions) {
						String viewNameWithExtension = viewName + '.' + extension;
						view = viewResolver.resolveViewName(viewNameWithExtension, locale);
						if (view != null) {
							candidateViews.add(view);
						}
					}
				}
			}
		}
		// 如果有默认视图，将默认视图也添加到 候选视图
		if (!CollectionUtils.isEmpty(this.defaultViews)) {
			candidateViews.addAll(this.defaultViews);
		}
		return candidateViews;
	}

	/**
	 * 从多个视图中找出最优视图
	 *
	 * @param candidateViews 候选视图
	 * @param requestedMediaTypes
	 * @param attrs
	 * @return
	 */
	@Nullable
	private View getBestView(List<View> candidateViews, List<MediaType> requestedMediaTypes, RequestAttributes attrs) {
		// 判断候选视图中有没有 redirect 视图，如果有直接将其返回
		for (View candidateView : candidateViews) {
			if (candidateView instanceof SmartView) {
				SmartView smartView = (SmartView) candidateView;
				if (smartView.isRedirectView()) {
					return candidateView;
				}
			}
		}
		for (MediaType mediaType : requestedMediaTypes) {
			for (View candidateView : candidateViews) {
				if (StringUtils.hasText(candidateView.getContentType())) {
					// 根据候选视图获取对应的 MediaType
					MediaType candidateContentType = MediaType.parseMediaType(candidateView.getContentType());
					// 判断当前 MediaType 是否支持从候选视图获取对应的 MediaType，如 text/* 可以支持 text/html、text/css、text/xml 等所有 text 的类型
					if (mediaType.isCompatibleWith(candidateContentType)) {
						if (logger.isDebugEnabled()) {
							logger.debug("Selected '" + mediaType + "' given " + requestedMediaTypes);
						}
						attrs.setAttribute(View.SELECTED_CONTENT_TYPE, mediaType, RequestAttributes.SCOPE_REQUEST);
						return candidateView;
					}
				}
			}
		}
		return null;
	}


	private static final View NOT_ACCEPTABLE_VIEW = new View() {

		@Override
		@Nullable
		public String getContentType() {
			return null;
		}

		@Override
		public void render(@Nullable Map<String, ?> model, HttpServletRequest request, HttpServletResponse response) {
			response.setStatus(HttpServletResponse.SC_NOT_ACCEPTABLE);
		}
	};

}
