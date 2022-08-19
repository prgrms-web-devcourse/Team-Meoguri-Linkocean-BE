package com.meoguri.linkocean.configuration;

import java.util.List;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.data.web.SortHandlerMethodArgumentResolver;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class WebConfiguration implements WebMvcConfigurer {

	private static final String ID = "id";
	private static final int MAX_PAGE_SIZE = 8;

	@Override
	public void addArgumentResolvers(final List<HandlerMethodArgumentResolver> resolvers) {
		/* set default pageable as Page request [number:0, size 8, sort: id: DESC] */
		SortHandlerMethodArgumentResolver sortArgumentResolver = new SortHandlerMethodArgumentResolver();
		sortArgumentResolver.setFallbackSort(Sort.by(Sort.Direction.DESC, ID));
		sortArgumentResolver.setSortParameter("order");

		PageableHandlerMethodArgumentResolver resolver =
			new PageableHandlerMethodArgumentResolver(sortArgumentResolver);
		resolver.setOneIndexedParameters(true);
		resolver.setMaxPageSize(MAX_PAGE_SIZE);
		resolvers.add(resolver);
	}

	@Override
	public void addViewControllers(final ViewControllerRegistry registry) {
		registry.addViewController("/api/v1/healthCheck").setViewName("health");
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
