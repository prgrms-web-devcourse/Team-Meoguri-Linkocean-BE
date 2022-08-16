package com.meoguri.linkocean.domain.user.entity;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import com.meoguri.linkocean.domain.user.entity.vo.Email;
import com.meoguri.linkocean.domain.user.entity.vo.OAuthType;

class UserTest {

	@ParameterizedTest
	@ValueSource(
		strings = {
			"GOOGLE",
			"NAVER",
			"KAKAO",
			"GITHUB"
		}
	)
	void 사용자_생성_성공(final String oAuthType) {
		//given
		final String email = "haha@papa.com";

		//when
		final User user = new User(email, oAuthType);

		//then
		assertThat(user).isNotNull()
			.extracting(User::getEmail, User::getOauthType)
			.containsExactly(new Email(email), OAuthType.of(oAuthType));
	}

	@Test
	void 유효_하지_않은_O_Auth_Type_사용자_생성_실패() {
		//given
		final String oAuthType = "unknownType";

		//when then
		assertThatIllegalArgumentException()
			.isThrownBy(() -> new User("crush@gmail.com", oAuthType));
	}
}
