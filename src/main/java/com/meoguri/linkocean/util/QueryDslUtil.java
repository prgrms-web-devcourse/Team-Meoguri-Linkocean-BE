package com.meoguri.linkocean.util;

import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.EntityPath;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPQLQuery;

import lombok.AllArgsConstructor;

public final class QueryDslUtil {

	/**
	 * 동적 where 절을 지원하기 위한 유틸리티 메서드
	 */
	public static BooleanBuilder nullSafeBuilder(final Supplier<BooleanExpression> cond) {
		try {
			return new BooleanBuilder(cond.get());
		} catch (IllegalArgumentException | NullPointerException e) {
			return new BooleanBuilder();
		}
	}

	/**
	 * 동적 join 을 지원하기 위한 유틸리티 메서드
	 */
	public static <T, P> JPQLQuery<T> joinIf(JPQLQuery<T> base,
		final JoinEntityPathStore<P> join, final List<Predicate> on, final Boolean when) {

		if (when) {
			base = base.join(join.entityPath, join.path)
				.on(on.toArray(Predicate[]::new));
		}
		return base;
	}

	public static <P> JoinEntityPathStore<P> join(final EntityPath<P> target, final Path<P> alias) {

		return new JoinEntityPathStore<>(target, alias);
	}

	public static boolean when(final boolean cond) {
		return cond;
	}

	public static List<Predicate> on(final Predicate... condition) {

		return Arrays.asList(condition);
	}

	@AllArgsConstructor
	private static class JoinEntityPathStore<P> {

		final EntityPath<P> entityPath;
		final Path<P> path;
	}
}
