package com.meoguri.linkocean.configuration.security.oauth;

import static org.assertj.core.api.Assertions.*;

import java.util.Map;

import org.junit.jupiter.api.Test;

import com.meoguri.linkocean.domain.user.entity.Email;
import com.meoguri.linkocean.domain.user.entity.User;
import com.meoguri.linkocean.domain.user.entity.User.OAuthType;

class OAuthAttributesTest {

	final Map<String, Object> googleAttributes = Map.of("email", "haha@gmail.com");
	final Map<String, Object> naverAttributes = Map.of("response", Map.of("email", "haha@naver.com"));
	final Map<String, Object> kakaoAttributes = Map.of("kakao_account", Map.of("email", "haha@kakao.com"));

	@Test
	void Google_OAuth_속성_생성_성공() {
		//given
		final String registrationId = "google";

		//when
		final OAuthAttributes oAuthAttributes = OAuthAttributes.of(registrationId, googleAttributes);

		//then
		assertThat(oAuthAttributes).isNotNull()
			.extracting(OAuthAttributes::getEmail, OAuthAttributes::getOAuthType)
			.containsExactly("haha@gmail.com", "GOOGLE");
	}

	@Test
	void Naver_OAuth_속성_생성_성공() {
		//given
		final String registrationId = "naver";

		//when
		final OAuthAttributes oAuthAttributes = OAuthAttributes.of(registrationId, naverAttributes);

		//then
		assertThat(oAuthAttributes).isNotNull()
			.extracting(OAuthAttributes::getEmail, OAuthAttributes::getOAuthType)
			.containsExactly("haha@naver.com", "NAVER");
	}

	@Test
	void Kakao_OAuth_속성_생성_성공() {
		//given
		final String registrationId = "kakao";

		//when
		final OAuthAttributes oAuthAttributes = OAuthAttributes.of(registrationId, kakaoAttributes);

		//then
		assertThat(oAuthAttributes).isNotNull()
			.extracting(OAuthAttributes::getEmail, OAuthAttributes::getOAuthType)
			.containsExactly("haha@kakao.com", "KAKAO");
	}

	@Test
	void OAuth_속성_생성_실패() {
		//given
		final String wrongRegistrationId = "wrongVendor";
		final Map<String, Object> attributes = Map.of("email", "haha@gmail.com");

		//when then
		assertThatIllegalStateException().isThrownBy(() -> OAuthAttributes.of(wrongRegistrationId, attributes));
	}

	@Test
	void Google_OAuth_속성으로_사용자_생성_성공() {
		//given
		final String registrationId = "google";
		final OAuthAttributes oAuthAttributes = OAuthAttributes.of(registrationId, googleAttributes);

		//when
		final User user = oAuthAttributes.toEntity();

		//then
		assertThat(user).isNotNull()
			.extracting(User::getEmail, User::getOAuthType)
			.containsExactly(new Email("haha@gmail.com"), OAuthType.GOOGLE);
	}

	@Test
	void Naver_OAuth_속성으로_사용자_생성_성공() {
		//given
		final String registrationId = "naver";
		final OAuthAttributes oAuthAttributes = OAuthAttributes.of(registrationId, naverAttributes);

		//when
		final User user = oAuthAttributes.toEntity();

		//then
		assertThat(user).isNotNull()
			.extracting(User::getEmail, User::getOAuthType)
			.containsExactly(new Email("haha@naver.com"), OAuthType.NAVER);
	}

	@Test
	void Kakao_OAuth_속성으로_사용자_생성_성공() {
		//given
		final String registrationId = "kakao";
		final OAuthAttributes oAuthAttributes = OAuthAttributes.of(registrationId, kakaoAttributes);

		//when
		final User user = oAuthAttributes.toEntity();

		//then
		assertThat(user).isNotNull()
			.extracting(User::getEmail, User::getOAuthType)
			.containsExactly(new Email("haha@kakao.com"), OAuthType.KAKAO);
	}

}
