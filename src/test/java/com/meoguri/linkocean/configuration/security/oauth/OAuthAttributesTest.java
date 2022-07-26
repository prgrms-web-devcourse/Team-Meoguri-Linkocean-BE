package com.meoguri.linkocean.configuration.security.oauth;

import static org.assertj.core.api.Assertions.*;

import java.util.Map;

import org.junit.jupiter.api.Test;

import com.meoguri.linkocean.domain.user.entity.User;
import com.meoguri.linkocean.domain.user.entity.vo.Email;
import com.meoguri.linkocean.domain.user.entity.vo.OAuthType;

class OAuthAttributesTest {

	@Test
	void OAuth_속성_생성_성공() {
		//given
		final String registrationId = "Google";
		final Map<String, Object> attributes = Map.of("email", "haha@gmail.com");

		//when
		final OAuthAttributes oAuthAttributes = OAuthAttributes.of(registrationId, attributes);

		//then
		assertThat(oAuthAttributes).isNotNull()
			.extracting(OAuthAttributes::getEmail, OAuthAttributes::getOAuthType)
			.containsExactly("haha@gmail.com", OAuthType.GOOGLE);
	}

	@Test
	void OAuth_속성으로_사용자_생성_성공() {
		//given
		final String registrationId = "Google";
		final Map<String, Object> attributes = Map.of("email", "haha@gmail.com");
		final OAuthAttributes oAuthAttributes = OAuthAttributes.of(registrationId, attributes);

		//when
		final User user = oAuthAttributes.toEntity();

		//then
		assertThat(user).isNotNull()
			.extracting(User::getEmail, User::getOAuthType)
			.containsExactly(new Email("haha@gmail.com"), OAuthType.GOOGLE);
	}
}
