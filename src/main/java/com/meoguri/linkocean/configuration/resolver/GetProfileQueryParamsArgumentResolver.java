package com.meoguri.linkocean.configuration.resolver;

import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class GetProfileQueryParamsArgumentResolver implements HandlerMethodArgumentResolver {

	@Override
	public boolean supportsParameter(final MethodParameter parameter) {
		return GetProfileQueryParams.class.isAssignableFrom(parameter.getParameterType());
	}

	@Override
	public Object resolveArgument(final MethodParameter parameter, final ModelAndViewContainer mavContainer,
		final NativeWebRequest webRequest, final WebDataBinderFactory binderFactory) {

		final String page = webRequest.getParameter("page");
		final String size = webRequest.getParameter("size");
		final String username = webRequest.getParameter("username");

		log.info("profile 조회 요청 : page : {}, size : {}, username : {}", page, size, username);
		return new GetProfileQueryParams(username);
	}
}
