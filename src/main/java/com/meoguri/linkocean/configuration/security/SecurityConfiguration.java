package com.meoguri.linkocean.configuration.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

import com.meoguri.linkocean.configuration.security.oauth.CustomOAuth2UserService;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class SecurityConfiguration {

	private final CustomOAuth2UserService customOAuth2UserService;

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		return http
			.csrf().disable()
			.headers().frameOptions().disable()
			.and()
			.authorizeRequests()
			.anyRequest().permitAll()
			.and()
			.logout()
			.logoutSuccessUrl("/")
			.and()
			.oauth2Login(oauth2 -> oauth2.userInfoEndpoint().userService(customOAuth2UserService))
			.build();
	}
}
