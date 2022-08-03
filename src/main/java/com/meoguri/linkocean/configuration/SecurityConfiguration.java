package com.meoguri.linkocean.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

import com.meoguri.linkocean.configuration.security.oauth.CustomAuthenticationEntryPoint;
import com.meoguri.linkocean.configuration.security.oauth.CustomOAuth2UserService;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class SecurityConfiguration {

	private final CustomOAuth2UserService customOAuth2UserService;
	private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint;

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		return http
			.csrf().disable()
			.headers().frameOptions().disable()
			.and()
			.formLogin().disable()
			.authorizeRequests()
			// .antMatchers("/", "/error", "/api/v1/healthCheck", "/api/v1/login").permitAll()
			.anyRequest().permitAll()
			.and()
			.logout()
			.logoutSuccessUrl("/")
			.and()
			.exceptionHandling()
			.authenticationEntryPoint(customAuthenticationEntryPoint)
			.and()
			.oauth2Login(oauth2 ->
					oauth2.userInfoEndpoint().userService(customOAuth2UserService)
				// .and()
				// .defaultSuccessUrl("/api/v1/login/success")
			)
			.build();
	}
}
