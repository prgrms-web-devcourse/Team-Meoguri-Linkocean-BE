package com.meoguri.linkocean.configuration;

import java.util.List;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
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
	public void addCorsMappings(final CorsRegistry registry) {
		registry.addMapping("/**")
			.allowedOrigins("*")
			.allowedMethods("GET", "POST");
	}

	// @Bean
	// public CorsConfigurationSource corsConfigurationSource() {
	// 	CorsConfiguration configuration = new CorsConfiguration();
	// 	configuration.addAllowedOrigin("*");
	// 	configuration.addAllowedMethod("*");
	// 	configuration.addAllowedHeader("*");
	// 	// configuration.setAllowCredentials(true);
	// 	configuration.setMaxAge(3600L);
	// 	UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
	// 	source.registerCorsConfiguration("/**", configuration);
	// 	return source;
	// }
}
