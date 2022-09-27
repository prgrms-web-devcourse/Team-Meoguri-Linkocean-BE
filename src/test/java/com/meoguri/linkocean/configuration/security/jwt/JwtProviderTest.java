package com.meoguri.linkocean.configuration.security.jwt;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import com.meoguri.linkocean.internal.user.domain.model.Email;
import com.meoguri.linkocean.internal.user.domain.model.OAuthType;

import io.jsonwebtoken.Claims;

@SpringJUnitConfig(JwtProviderTest.Config.class)
class JwtProviderTest {

	@Autowired
	private JwtProvider jwtProvider;

	@Test
	void access_token_발급_성공() {
		//given
		final Email email = new Email("mail@gmail.com");
		final OAuthType google = OAuthType.GOOGLE;

		//when
		final String accessToken = jwtProvider.generate(email, google);

		//then
		assertAll(
			() -> assertThat(accessToken).isNotBlank(),
			() -> assertThat(jwtProvider.getClaims(accessToken, Claims::getId)).isEqualTo(Email.toString(email)),
			() -> assertThat(jwtProvider.getClaims(accessToken, Claims::getAudience)).isEqualTo(google.toString())
		);
	}

	@TestConfiguration
	static class Config {

		@Bean
		public JwtProperties jwtProperties() {
			final JwtProperties properties = new JwtProperties();
			properties.setSecretKey("test secretKey");
			properties.setExpiration(10000L);
			return properties;
		}

		@Bean
		public JwtProvider jwtProvider(final JwtProperties jwtProperties) {
			return new JwtProvider(jwtProperties);
		}
	}
}
