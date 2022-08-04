package com.meoguri.linkocean.configuration;

import java.util.List;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.meoguri.linkocean.configuration.security.oauth.LoginUserArgumentResolver;
import com.meoguri.linkocean.controller.bookmark.support.GetBookmarkQueryParamsArgumentResolver;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class WebConfiguration implements WebMvcConfigurer {

	private final LoginUserArgumentResolver loginUserArgumentResolver;
	private final GetBookmarkQueryParamsArgumentResolver getBookmarkQueryParamsArgumentResolver;

	@Override
	public void addArgumentResolvers(final List<HandlerMethodArgumentResolver> resolvers) {
		resolvers.add(loginUserArgumentResolver);
		resolvers.add(getBookmarkQueryParamsArgumentResolver);
	}

	@Override
	public void addViewControllers(final ViewControllerRegistry registry) {
		registry.addViewController("/api/v1/healthCheck").setViewName("health");
	}

	@Override
	public void addCorsMappings(final CorsRegistry registry) {
		registry.addMapping("/**")
			.allowedOrigins("*")
			.allowedMethods("GET", "POST");
	}

}
