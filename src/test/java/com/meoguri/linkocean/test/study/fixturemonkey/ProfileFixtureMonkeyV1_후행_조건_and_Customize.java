package com.meoguri.linkocean.test.study.fixturemonkey;

import static com.meoguri.linkocean.internal.bookmark.entity.vo.Category.*;
import static com.meoguri.linkocean.internal.profile.entity.Profile.*;
import static org.assertj.core.api.AssertionsForInterfaceTypes.*;

import java.util.List;

import org.junit.jupiter.api.RepeatedTest;

import com.meoguri.linkocean.internal.profile.entity.FavoriteCategories;
import com.meoguri.linkocean.internal.profile.entity.Profile;

import com.navercorp.fixturemonkey.FixtureMonkey;
import com.navercorp.fixturemonkey.generator.FieldReflectionArbitraryGenerator;

/**
 * 후행 조건을 이용해 단순 필드의 생성 제어
 */
class ProfileFixtureMonkeyV1_후행_조건_and_Customize {

	@RepeatedTest(100)
	void 후행_조건을_이용해_id_값의_범위_제한() {
		//given
		final FixtureMonkey fixture = FixtureMonkey.builder()
			.defaultGenerator(FieldReflectionArbitraryGenerator.INSTANCE)
			.build();

		//when
		final Profile actual = fixture.giveMeBuilder(Profile.class)
			// Predicate 을 받는 후행 조건 적용 메서드의 경우 not null 을 적용해 주어야 함
			// .setNotNull("id")

			// 후행 조건 여러번 적용 할 경우 마지막으로 적용한 후행 조건이 적용된다
			// .setPostCondition(p -> p.getId() >= 1)
			.setPostCondition("id", Long.class, it -> it >= 1)
			// .setPostCondition("id", Long.class, it -> it >= 1, 1)

			.sample();

		//then
		assertThat(actual.getId()).isGreaterThanOrEqualTo(1);
	}

	@RepeatedTest(100)
	void 후행_조건을_이용해_문자열의_길이_제한_필드_하나하나() {
		//given
		final FixtureMonkey fixture = FixtureMonkey.builder()
			.defaultGenerator(FieldReflectionArbitraryGenerator.INSTANCE)
			.build();

		//when
		final Profile actual = fixture.giveMeBuilder(Profile.class)
			.setPostCondition("username", String.class, it -> it.length() <= MAX_PROFILE_USERNAME_LENGTH)
			.setPostCondition("bio", String.class, it -> it.length() <= MAX_PROFILE_BIO_LENGTH)
			.setPostCondition("image", String.class, it -> it.length() <= MAX_PROFILE_IMAGE_URL_LENGTH)
			.sample();

		//then
		assertThat(actual.getUsername()).hasSizeLessThanOrEqualTo(MAX_PROFILE_USERNAME_LENGTH);
		assertThat(actual.getBio()).hasSizeLessThanOrEqualTo(MAX_PROFILE_BIO_LENGTH);
		assertThat(actual.getImage()).hasSizeLessThanOrEqualTo(MAX_PROFILE_IMAGE_URL_LENGTH);
	}

	@RepeatedTest(100)
	void 후행_조건을_이용해_문자열의_길이_제한_루트에서_한번에() {
		//given
		final FixtureMonkey fixture = FixtureMonkey.builder()
			.defaultGenerator(FieldReflectionArbitraryGenerator.INSTANCE)
			.build();

		//when
		//이러면 null 을 postCondition 자체에서 잡아주지는 않음
		//이런 방식이 있다는 것만 알아두고 사용했을때 이쁠거 같으면 사용하자
		final Profile actual = fixture.giveMeBuilder(Profile.class)
			.setNotNull("username")
			.setNotNull("bio")
			.setNotNull("image")
			.setPostCondition("$", Profile.class, it ->
				it.getUsername().length() <= MAX_PROFILE_USERNAME_LENGTH
					&& it.getBio().length() <= MAX_PROFILE_BIO_LENGTH
					&& it.getImage().length() <= MAX_PROFILE_IMAGE_URL_LENGTH
			)
			.sample();

		//then
		assertThat(actual.getUsername()).hasSizeLessThanOrEqualTo(MAX_PROFILE_USERNAME_LENGTH);
		assertThat(actual.getBio()).hasSizeLessThanOrEqualTo(MAX_PROFILE_BIO_LENGTH);
		assertThat(actual.getImage()).hasSizeLessThanOrEqualTo(MAX_PROFILE_IMAGE_URL_LENGTH);
	}

	@RepeatedTest(100)
	void customize() {
		//given
		final FixtureMonkey fixture = FixtureMonkey.builder()
			.defaultGenerator(FieldReflectionArbitraryGenerator.INSTANCE)
			.build();

		//when
		final Profile actual = fixture.giveMeBuilder(Profile.class)
			.customize(Profile.class, p -> {
				p.update("haha", "bio", "image", new FavoriteCategories(List.of(IT)));
				return p;
			}).sample();

		assertThat(actual.getUsername()).isEqualTo("haha");
		assertThat(actual.getBio()).isEqualTo("bio");
		assertThat(actual.getImage()).isEqualTo("image");
	}
}
