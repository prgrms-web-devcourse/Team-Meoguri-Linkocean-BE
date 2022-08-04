package com.meoguri.linkocean.configuration;

import java.util.List;

import org.springframework.boot.web.server.Cookie;
import org.springframework.boot.web.servlet.server.CookieSameSiteSupplier;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.meoguri.linkocean.configuration.security.oauth.LoginUserArgumentResolver;
import com.meoguri.linkocean.controller.bookmark.support.GetBookmarkQueryParamsArgumentResolver;
import com.meoguri.linkocean.controller.profile.support.GetProfileQueryParamsArgumentResolver;
import com.meoguri.linkocean.controller.profile.support.ProfileSearchTabConverterFactory;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class WebConfiguration implements WebMvcConfigurer {

	private final LoginUserArgumentResolver loginUserArgumentResolver;
	private final GetBookmarkQueryParamsArgumentResolver getBookmarkQueryParamsArgumentResolver;
	private final GetProfileQueryParamsArgumentResolver getProfileQueryParamsArgumentResolver;

	@Override
	public void addArgumentResolvers(final List<HandlerMethodArgumentResolver> resolvers) {
		resolvers.add(loginUserArgumentResolver);
		resolvers.add(getBookmarkQueryParamsArgumentResolver);
		resolvers.add(getProfileQueryParamsArgumentResolver);
	}

	@Override
	public void addViewControllers(final ViewControllerRegistry registry) {
		registry.addViewController("/api/v1/healthCheck").setViewName("health");
	}

	@Override
	public void addCorsMappings(final CorsRegistry registry) {
		registry.addMapping("/**")
			.allowedOrigins("http://localhost:8080", "http://localhost:3000")
			.allowedMethods("GET", "POST")
			.allowCredentials(true); //쿠키 요청을 허용한다(다른 도메인 서버에 인증하는 경우에만 사용해야하며, true 설정시 보안상 이슈가 발생할 수 있다)
	}

	@Override
	public void addFormatters(final FormatterRegistry registry) {
		registry.addConverterFactory(new ProfileSearchTabConverterFactory());
	}

	/*	@Bean
	public CookieSerializer cookieSerializer() throws MalformedURLException {
		DefaultCookieSerializer serializer = new DefaultCookieSerializer();
		serializer.setCookieName("JSESSIONID");
		serializer.setSameSite("");
		return serializer;
	}*/

	// @Bean
	public CookieSameSiteSupplier applicationCookieSameSiteSupplier() {
		return CookieSameSiteSupplier.of(Cookie.SameSite.valueOf(""));
	}

}
