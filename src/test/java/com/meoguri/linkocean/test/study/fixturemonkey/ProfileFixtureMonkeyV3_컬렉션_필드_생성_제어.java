package com.meoguri.linkocean.test.study.fixturemonkey;

import static org.assertj.core.api.AssertionsForInterfaceTypes.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.RandomUtils;
import org.junit.jupiter.api.RepeatedTest;
import org.springframework.test.util.ReflectionTestUtils;

import com.meoguri.linkocean.domain.bookmark.entity.vo.Category;
import com.meoguri.linkocean.domain.profile.entity.FavoriteCategories;
import com.meoguri.linkocean.domain.profile.entity.Profile;

import com.navercorp.fixturemonkey.FixtureMonkey;
import com.navercorp.fixturemonkey.generator.FieldReflectionArbitraryGenerator;

/**
 * 컬렉션 필드의 생성 제어
 */
class ProfileFixtureMonkeyV3_컬렉션_필드_생성_제어 {

	@RepeatedTest(100)
	void 필드의_컬렉션_크기를_제어하는_연산() {
		//given
		final int maxFollowSize = 5;
		final FixtureMonkey fixture = FixtureMonkey.builder()
			.defaultGenerator(FieldReflectionArbitraryGenerator.INSTANCE)
			.build();

		//when
		//하지만 의미 있는 follow 정보가 들어가지는 않음
		//결국 의미 있는 데이터를 위해서는 FieldReflectionArbitraryGenerator 이 아닌
		//ConstructorPropertiesGenerator, 사용이 필요할 것 같음 - 조금더 어려워 보임
		final Profile actual = fixture.giveMeBuilder(Profile.class)
			.size("follow", maxFollowSize)
			.sample();

		//then
		assertThat(actual.getFollows()).hasSizeLessThanOrEqualTo(maxFollowSize);
	}

	// 단순 한 사용으로는 TooManyFilterMissesException 발생
	// 내부 로직에 따라 랜덤 트라이로 1 ~ 12 크기 제한을 계속 시도하면 10000 번 시도 해도 만족 하지 않는 경우가 발생
	@RepeatedTest(100)
	void 필드의_컬렉션_크기를_제어하는_연산_선호카테고리() {
		//given
		final int minFavoriteCategorySize = 1;
		final int maxFavoriteCategorySize = 2;
		final FixtureMonkey fixture = FixtureMonkey.builder()
			.defaultGenerator(FieldReflectionArbitraryGenerator.INSTANCE)
			.build();

		//when
		final Profile actual = fixture.giveMeBuilder(Profile.class)
			.size("favoriteCategories.favoriteCategories", minFavoriteCategorySize, maxFavoriteCategorySize)
			.sample();

		//then
		final FavoriteCategories favoriteCategories = actual.getFavoriteCategories();
		final Set<Category> categorySet =
			(HashSet<Category>)ReflectionTestUtils.getField(favoriteCategories, "favoriteCategories");
		assertThat(categorySet).hasSizeBetween(minFavoriteCategorySize, maxFavoriteCategorySize);
	}

	@RepeatedTest(100)
	void name() {
		//given
		final int minFavoriteCategorySize = 1;
		final int maxFavoriteCategorySize = Category.totalCount();
		final FixtureMonkey fixture = FixtureMonkey.builder()
			.defaultGenerator(FieldReflectionArbitraryGenerator.INSTANCE)
			.build();

		List<Category> allCategories = Arrays.stream(Category.values()).collect(Collectors.toList());
		Collections.shuffle(allCategories);
		allCategories = new ArrayList<>(
			allCategories.subList(0, RandomUtils.nextInt(minFavoriteCategorySize, maxFavoriteCategorySize)));

		//when
		final Profile actual = fixture.giveMeBuilder(Profile.class)
			.set("favoriteCategories", new FavoriteCategories(allCategories))
			.sample();

		//then
		final FavoriteCategories favoriteCategories = actual.getFavoriteCategories();
		final Set<Category> categorySet =
			(HashSet<Category>)ReflectionTestUtils.getField(favoriteCategories, "favoriteCategories");
		assertThat(categorySet).hasSizeBetween(1, maxFavoriteCategorySize);
	}
}
