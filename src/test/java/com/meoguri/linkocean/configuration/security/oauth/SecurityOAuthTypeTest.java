package com.meoguri.linkocean.configuration.security.oauth;

import static com.meoguri.linkocean.configuration.security.oauth.SecurityOAuthType.*;
import static org.assertj.core.api.Assertions.*;

import java.util.Map;

import org.junit.jupiter.api.Test;

import com.meoguri.linkocean.internal.user.domain.model.Email;
import com.meoguri.linkocean.internal.user.domain.model.OAuthType;

class SecurityOAuthTypeTest {

	@Test
	void Google_OAuth_이메일_파싱() {
		//given
		final Map<String, Object> attributes = Map.of("email", "haha@gmail.com");
		final SecurityOAuthType securityOAuthType = GOOGLE;

		//when
		final Email email = securityOAuthType.parseEmail(attributes);
		final OAuthType oAuthType = securityOAuthType.getOAuthType();

		//then
		assertThat(email).isEqualTo(new Email("haha@gmail.com"));
		assertThat(oAuthType).isEqualTo(OAuthType.GOOGLE);
	}

	@Test
	void Naver_OAuth_이메일_파싱() {
		//given
		final Map<String, Object> attributes = Map.of("response", Map.of("email", "haha@naver.com"));
		final SecurityOAuthType securityOAuthType = NAVER;

		//when
		final Email email = securityOAuthType.parseEmail(attributes);
		final OAuthType oAuthType = securityOAuthType.getOAuthType();

		//then
		assertThat(email).isEqualTo(new Email("haha@naver.com"));
		assertThat(oAuthType).isEqualTo(OAuthType.NAVER);
	}

	@Test
	void Kakao_OAuth_이메일_파싱() {
		//given
		final Map<String, Object> attributes = Map.of("kakao_account", Map.of("email", "haha@kakao.com"));
		final SecurityOAuthType securityOAuthType = KAKAO;

		//when
		final Email email = securityOAuthType.parseEmail(attributes);
		final OAuthType oAuthType = securityOAuthType.getOAuthType();

		//then
		assertThat(email).isEqualTo(new Email("haha@kakao.com"));
		assertThat(oAuthType).isEqualTo(OAuthType.KAKAO);
	}

}
