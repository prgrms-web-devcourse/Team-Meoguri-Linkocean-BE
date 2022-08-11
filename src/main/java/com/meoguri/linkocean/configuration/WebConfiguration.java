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

import com.meoguri.linkocean.configuration.resolver.GetBookmarkQueryParamsArgumentResolver;
import com.meoguri.linkocean.configuration.resolver.GetProfileQueryParamsArgumentResolver;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class WebConfiguration implements WebMvcConfigurer {

	private final GetBookmarkQueryParamsArgumentResolver getBookmarkQueryParamsArgumentResolver;
	private final GetProfileQueryParamsArgumentResolver getProfileQueryParamsArgumentResolver;

	@Override
	public void addArgumentResolvers(final List<HandlerMethodArgumentResolver> resolvers) {
		/* add query parameter argument resolvers */
		resolvers.add(getBookmarkQueryParamsArgumentResolver);
		resolvers.add(getProfileQueryParamsArgumentResolver);

		/* set default pageable as Page request [number:0, size 8, sort: id: DESC] */
		SortHandlerMethodArgumentResolver sortArgumentResolver = new SortHandlerMethodArgumentResolver();
		sortArgumentResolver.setFallbackSort(Sort.by(Sort.Direction.DESC, "id"));

		PageableHandlerMethodArgumentResolver resolver =
			new PageableHandlerMethodArgumentResolver(sortArgumentResolver);
		resolver.setOneIndexedParameters(true);
		resolver.setMaxPageSize(8);
		resolvers.add(resolver);
	}

	@Override
	public void addViewControllers(final ViewControllerRegistry registry) {
		registry.addViewController("/api/v1/healthCheck").setViewName("health");
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
