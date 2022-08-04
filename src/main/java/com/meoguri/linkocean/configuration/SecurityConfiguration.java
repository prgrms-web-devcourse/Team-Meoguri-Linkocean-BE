package com.meoguri.linkocean.configuration;

import javax.servlet.http.HttpServletResponse;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.meoguri.linkocean.configuration.security.jwt.JwtAuthenticationFilter;
import com.meoguri.linkocean.configuration.security.oauth.CustomOAuth2UserService;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class SecurityConfiguration {

	private final CustomOAuth2UserService customOAuth2UserService;

	private final JwtAuthenticationFilter jwtAuthenticationFilter;

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		return http
			.csrf().disable()
			.formLogin().disable()
			.httpBasic().disable()
			.headers().disable()

			.authorizeRequests()
			// .antMatchers("/", "/error", "/api/v1/healthCheck", "/api/v1/login").permitAll()
			.anyRequest().permitAll()
			.and()
			.logout()
			.logoutSuccessUrl("/")
			.and()
			.oauth2Login(oauth2 ->
				oauth2.userInfoEndpoint().userService(customOAuth2UserService)
			)
			.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
			.exceptionHandling()
			.authenticationEntryPoint(
				(request, response, authException) -> response.setStatus(HttpServletResponse.SC_UNAUTHORIZED))
			.accessDeniedHandler(
				(request, response, accessDeniedException) -> response.setStatus(HttpServletResponse.SC_UNAUTHORIZED))
			.and()
			.build();
	}
}
