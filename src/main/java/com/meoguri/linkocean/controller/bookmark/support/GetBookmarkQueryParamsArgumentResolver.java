package com.meoguri.linkocean.controller.bookmark.support;

import static org.apache.commons.lang3.BooleanUtils.*;
import static org.apache.commons.lang3.math.NumberUtils.*;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

@Component
public class GetBookmarkQueryParamsArgumentResolver implements HandlerMethodArgumentResolver {

	private static final int DEFAULT_PAGE = 1;
	private static final int DEFAULT_SIZE = 8;
	private static final String DEFAULT_ORDER = "upload";

	@Override
	public boolean supportsParameter(final MethodParameter parameter) {
		return GetBookmarkQueryParams.class.isAssignableFrom(parameter.getParameterType());
	}

	@Override
	public Object resolveArgument(final MethodParameter parameter, final ModelAndViewContainer mavContainer,
		final NativeWebRequest webRequest, final WebDataBinderFactory binderFactory) throws Exception {

		final String page = webRequest.getParameter("page");
		final String size = webRequest.getParameter("size");
		final String order = webRequest.getParameter("order");
		final String category = webRequest.getParameter("category");
		final String searchTitle = webRequest.getParameter("searchTitle");
		final String favorite = webRequest.getParameter("favorite");
		final String follow = webRequest.getParameter("follow");
		final String tags = webRequest.getParameter("tags");

		return new GetBookmarkQueryParams(
			toInt(page, DEFAULT_PAGE),
			toInt(size, DEFAULT_SIZE),
			Optional.ofNullable(order).orElse(DEFAULT_ORDER),
			category,
			searchTitle,
			toBoolean(favorite),
			toBoolean(follow),
			toTagList(tags)
		);
	}

	private List<String> toTagList(final String tags) {
		if (Objects.isNull(tags)) {
			return null;
		}
		return Arrays.stream(tags.split(",")).collect(Collectors.toList());
	}
}
