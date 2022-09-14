package com.meoguri.linkocean.test.study.fixturemonkey;

import static com.meoguri.linkocean.domain.profile.entity.Profile.*;
import static org.assertj.core.api.AssertionsForInterfaceTypes.*;

import org.junit.jupiter.api.RepeatedTest;

import net.jqwik.api.Arbitraries;

import com.meoguri.linkocean.domain.profile.entity.Profile;

import com.navercorp.fixturemonkey.ArbitraryBuilder;
import com.navercorp.fixturemonkey.FixtureMonkey;
import com.navercorp.fixturemonkey.customizer.ExpressionSpec;
import com.navercorp.fixturemonkey.generator.FieldReflectionArbitraryGenerator;

/**
 * set 이용한 단순 필드의 생성 제어
 */
class ProfileFixtureMonkeyV2_단순_필드_생성_제어 {

	@RepeatedTest(100)
	void set_이용해_id_값의_범위_제한() {
		//given
		final FixtureMonkey fixture = FixtureMonkey.builder()
			.defaultGenerator(FieldReflectionArbitraryGenerator.INSTANCE)
			.build();

		//when
		final Profile actual = fixture.giveMeBuilder(Profile.class)
			.set("id", Arbitraries.longs().greaterOrEqual(1))
			.sample();

		//then
		assertThat(actual.getId()).isGreaterThanOrEqualTo(1);
	}

	@RepeatedTest(100)
	void set_이용해_문자열의_길이_제한_필드_하나하나() {
		//given
		final FixtureMonkey fixture = FixtureMonkey.builder()
			.defaultGenerator(FieldReflectionArbitraryGenerator.INSTANCE)
			.build();

		//when
		final Profile actual = fixture.giveMeBuilder(Profile.class)
			.set("username", Arbitraries.strings().ofMaxLength(MAX_PROFILE_USERNAME_LENGTH))
			.set("bio", Arbitraries.strings().ofMaxLength(MAX_PROFILE_BIO_LENGTH))
			.set("image", Arbitraries.strings().ofMaxLength(MAX_PROFILE_IMAGE_URL_LENGTH))
			.sample();

		//then
		assertThat(actual.getUsername()).hasSizeLessThanOrEqualTo(MAX_PROFILE_USERNAME_LENGTH);
		assertThat(actual.getBio()).hasSizeLessThanOrEqualTo(MAX_PROFILE_BIO_LENGTH);
		assertThat(actual.getImage()).hasSizeLessThanOrEqualTo(MAX_PROFILE_IMAGE_URL_LENGTH);
	}

	/**
	 * expression Spec 으로 set 연산을 묶어서 처리 할 수 있음을 확인하자
	 * *
	 * 또한  set 을 활용하는 다양한 방식도 확인하자
	 * 1. username - 객체를 고정해서 넘길 수 있다.
	 * 2. bio - Arbitrary 로 설정 할 수 있다.
	 * 3. image - setBuilder 를 통해 다른 빌더를 넘길 수 있다.
	 */
	@RepeatedTest(100)
	void set_이용해_문자열의_길이_ExpressionSpec_으로_한번에() {
		//given
		final FixtureMonkey fixture = FixtureMonkey.builder()
			.defaultGenerator(FieldReflectionArbitraryGenerator.INSTANCE)
			.build();

		final ArbitraryBuilder<String> imageArbitraryBuilder = fixture
			.giveMeBuilder(String.class)
			.set("$", Arbitraries.strings().ofMaxLength(MAX_PROFILE_IMAGE_URL_LENGTH));

		//when
		final Profile actual = fixture.giveMeBuilder(Profile.class)
			.set(new ExpressionSpec()
				.set("username", "haha")
				.set("bio", Arbitraries.strings().ofMaxLength(MAX_PROFILE_BIO_LENGTH))
				.setBuilder("image", imageArbitraryBuilder)
			)
			.sample();

		//then
		assertThat(actual.getUsername()).isEqualTo("haha");
		assertThat(actual.getBio()).hasSizeLessThanOrEqualTo(MAX_PROFILE_BIO_LENGTH);
		assertThat(actual.getImage()).hasSizeLessThanOrEqualTo(MAX_PROFILE_IMAGE_URL_LENGTH);
	}
}
