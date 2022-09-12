package com.meoguri.linkocean.configuration;

import static javax.servlet.http.HttpServletResponse.*;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
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
			// 사용하지 않는 필터 disable
			.csrf().disable()
			.formLogin().disable()
			.httpBasic().disable()
			.headers().disable()
			.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
			.and()
			.logout(
				logout -> logout.logoutSuccessUrl("/")
			)
			.authorizeRequests(
				auth -> auth.anyRequest().permitAll()
			)
			.oauth2Login(oauth2 -> oauth2
				.userInfoEndpoint().userService(customOAuth2UserService)
			)
			.exceptionHandling(ex -> ex
				.authenticationEntryPoint((request, response, authException) -> response.setStatus(SC_UNAUTHORIZED))
				.accessDeniedHandler((request, response, accessDeniedException) -> response.setStatus(SC_UNAUTHORIZED))
			)
			.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
			.build();
	}
}
