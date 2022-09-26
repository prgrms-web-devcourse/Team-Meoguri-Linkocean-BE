package com.meoguri.linkocean.test.study.fixturemonkey;

import static org.assertj.core.api.AssertionsForInterfaceTypes.*;

import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.RepeatedTest;
import org.springframework.test.util.ReflectionTestUtils;

import com.meoguri.linkocean.internal.profile.entity.Profile;

import com.navercorp.fixturemonkey.FixtureMonkey;
import com.navercorp.fixturemonkey.generator.BeanArbitraryGenerator;
import com.navercorp.fixturemonkey.generator.FieldReflectionArbitraryGenerator;

/**
 * 픽스처 몽키 깡통 테스트
 */
class ProfileFixtureMonkeyV0_Generator전략 {

	/**
	 * setter 가 없기 때문에 BeanArbitraryGenerator 를 사용 할 수 없음
	 * 객체 필드는 전부 null 로 채워지고 컨테이너 필드 (Set) 는 empty set 으로 채워줌
	 */
	@RepeatedTest(100)
	void 프로필을_만들어_보자_깡통_BeanArbitraryGenerator() {
		//given
		final FixtureMonkey fixture = FixtureMonkey.builder()
			.defaultGenerator(BeanArbitraryGenerator.INSTANCE)  // 원래 default 가 BeanArbitraryGenerator 임
			.build();

		//when
		final Profile actual = fixture.giveMeOne(Profile.class);

		//then
		assertThat(actual.getId()).isNull();
		assertThat(actual.getUsername()).isNull();
		assertThat(actual.getBio()).isNull();

		assertEmptyContainer(actual.getFavoriteCategories(), "favoriteCategories");
		assertEmptyContainer(actual.getFavoriteBookmarkIds(), "favoriteBookmarkIds");
		assertEmptyContainer(actual, "follows");
	}

	private void assertEmptyContainer(final Object actual, final String fieldName) {
		assertThat(actual).isNotNull();
		final Set<?> containerField = (HashSet<?>)ReflectionTestUtils.getField(actual, fieldName);
		assertThat(containerField).isEmpty();
	}

	/**
	 * null 이 확률적으로 주입 되기 때문에 아무것도 검증 할 수 없음
	 * 하지만 채워지긴 잘 채워줌!
	 */
	@RepeatedTest(100)
	void 프로필을_만들어_보자_FieldReflection() {
		//given
		final FixtureMonkey fixture = FixtureMonkey.builder()
			.defaultGenerator(FieldReflectionArbitraryGenerator.INSTANCE)
			// jpa 와 비슷한 방식으로 주입해주는 FieldReflectionArbitraryGenerator
			.build();

		//when
		final Profile actual = fixture.giveMeOne(Profile.class);

		//then
		assertNoting(actual.getId());
		assertNoting(actual.getUsername());
		assertNoting(actual.getImage());

		assertNoting(actual.getFavoriteCategories(), "favoriteCategories");
		assertNoting(actual.getFavoriteBookmarkIds(), "favoriteBookmarkIds");
		assertNoting(actual, "follows");
	}

	private void assertNoting(final Object actual) {
		if (actual == null) {
			assertThat(actual).isNull();
		} else {
			assertThat(actual).isNotNull();
		}
	}

	private void assertNoting(final Object actual, final String fieldName) {
		if (actual == null) {
			assertThat(actual).isNull();
		} else {
			assertThat(actual).isNotNull();
			final Set<?> containerField = (HashSet<?>)ReflectionTestUtils.getField(actual, fieldName);
			assertThat(containerField).isNotNull();
		}
	}

}
