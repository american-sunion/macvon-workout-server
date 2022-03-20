package com.macvon.interceptors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.javasimon.SimonManager;
import org.javasimon.Split;
import org.javasimon.utils.SimonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Controller;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import org.springframework.web.servlet.resource.ResourceHttpRequestHandler;

import com.macvon.utils.AppUtils;
import com.macvon.utils.RandomUtils;

public class RequestInterceptor extends HandlerInterceptorAdapter {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	private static final String STOPWATCH_ATTRIBUTE = "WebRequestInterceptor-STOPWATCH";

	@Override
	public boolean preHandle(final HttpServletRequest request, final HttpServletResponse response, final Object handler)
			throws Exception {
		try {
			Class<? extends Object> handlerCls = null;
			if(handler instanceof ResourceHttpRequestHandler) {
				handlerCls = ((ResourceHttpRequestHandler) handler).getClass().getClass();
			} else if(handler instanceof ResourceHttpRequestHandler) {
				handlerCls = ((HandlerMethod) handler).getBean().getClass();
			}
			if(handlerCls!=null) {
				final Controller controllerAnnotation = handlerCls.getAnnotation(Controller.class);
				request.setAttribute(STOPWATCH_ATTRIBUTE,
						SimonManager.getStopwatch(this.getClass().getName() + "-" + handlerCls.getCanonicalName()).start());
				if (controllerAnnotation != null) {
					request.setAttribute("ObjectId", controllerAnnotation.value());
				}
				MDC.clear();
				String requestId = RandomUtils.generateRandom(12);
				MDC.put("ip", AppUtils.getIpAddr(request));
				MDC.put("requestId", String.valueOf(requestId));
				logger.info("[REQUEST] START - URL:[{}?{}] Handler:[{}] ", request.getRequestURL(), request.getQueryString(),
						handlerCls.getName());	
			} else {
				request.setAttribute(STOPWATCH_ATTRIBUTE,
						SimonManager.getStopwatch(this.getClass().getName()).start());
				MDC.clear();
				String requestId = RandomUtils.generateRandom(12);
				MDC.put("ip", AppUtils.getIpAddr(request));
				MDC.put("requestId", String.valueOf(requestId));
				logger.info("[REQUEST] START - URL:[{}?{}] ", request.getRequestURL(), request.getQueryString());	
			}

		} catch(Exception e) {
			logger.error("preHandle error {}", e.getMessage());
		}
		return Boolean.TRUE;
	}

	@Override
	public void afterCompletion(final HttpServletRequest request, final HttpServletResponse response,
			final Object handler, final Exception ex) throws Exception {
		super.afterCompletion(request, response, handler, ex);

		try {

			final Split split = (Split) request.getAttribute(STOPWATCH_ATTRIBUTE);
			if (split != null) {
				logger.info("[REQUEST] COMPLETE - URL:[{}?{}] Handler:[{}] Duration:[{}]", request.getRequestURL(),
						request.getQueryString(), handler.getClass().getName(),
						SimonUtils.presentNanoTime(split.runningFor()));
			}
		} catch (final Exception e) {
		} finally {
			request.removeAttribute(STOPWATCH_ATTRIBUTE);
		}
	}
}
