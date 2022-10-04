package com.meoguri.linkocean.configuration;

import java.util.List;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.data.web.SortHandlerMethodArgumentResolver;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.meoguri.linkocean.support.controller.converter.StringToEnumConverterFactory;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class WebConfiguration implements WebMvcConfigurer {

	private static final String ID = "id";
	private static final String SORT_PARAMETER = "order";

	private static final int MAX_PAGE_SIZE = 20;

	@Override
	public void addArgumentResolvers(final List<HandlerMethodArgumentResolver> resolvers) {
		/* set default pageable as Page request [number:0, size 8, sort: id: DESC] */
		SortHandlerMethodArgumentResolver sortArgumentResolver = new SortHandlerMethodArgumentResolver();
		sortArgumentResolver.setFallbackSort(Sort.by(Sort.Direction.DESC, ID));
		sortArgumentResolver.setSortParameter(SORT_PARAMETER);

		PageableHandlerMethodArgumentResolver resolver =
			new PageableHandlerMethodArgumentResolver(sortArgumentResolver);
		resolver.setOneIndexedParameters(true);
		resolver.setMaxPageSize(MAX_PAGE_SIZE);
		resolvers.add(resolver);
	}

	@Override
	public void addFormatters(final FormatterRegistry registry) {
		registry.addConverterFactory(new StringToEnumConverterFactory());
	}

	/* 정적인 페이지에 대한 뷰 컨트롤러 추가 */
	@Override
	public void addViewControllers(final ViewControllerRegistry registry) {
		registry.addRedirectViewController("/api/v1/healthCheck", "/health.html");
		registry.addRedirectViewController("/api/v1/docs", "/docs/index.html");
	}

	@Override
	public void addCorsMappings(final CorsRegistry registry) {
		registry.addMapping("/**")
			.allowedOrigins("*")
			.allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
			.exposedHeaders("*")
			.allowedHeaders("*");
	}
}
