package com.meoguri.linkocean.configuration.resolver;

import static org.apache.commons.lang3.math.NumberUtils.*;

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

	private static final int DEFAULT_PAGE = 1;
	private static final int DEFAULT_SIZE = 8;

	@Override
	public boolean supportsParameter(final MethodParameter parameter) {
		return GetProfileQueryParams.class.isAssignableFrom(parameter.getParameterType());
	}

	@Override
	public Object resolveArgument(final MethodParameter parameter, final ModelAndViewContainer mavContainer,
		final NativeWebRequest webRequest, final WebDataBinderFactory binderFactory) throws Exception {

		final String page = webRequest.getParameter("page");
		final String size = webRequest.getParameter("size");
		final String username = webRequest.getParameter("username");

		log.info("profile 조회 요청 : page : {}, size : {}, username : {}", page, size, username);
		return new GetProfileQueryParams(
			toInt(page, DEFAULT_PAGE),
			toInt(size, DEFAULT_SIZE),
			username
		);
	}
}
