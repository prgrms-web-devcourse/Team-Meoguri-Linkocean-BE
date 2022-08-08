package com.meoguri.linkocean.util;

import static lombok.AccessLevel.*;

import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPQLQuery;

import lombok.NoArgsConstructor;

@NoArgsConstructor(access = PRIVATE)
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
	public static <T> JPQLQuery<T> joinIf(
		JPQLQuery<T> base,
		final Supplier<JoinInfoListBuilder> joinInfoListBuilder,
		final List<Predicate> on,
		final boolean when
	) {

		if (when) {
			final List<JoinInfo> joinInfoList = joinInfoListBuilder.get().build();
			for (JoinInfo joinInfo : joinInfoList) {

				if (joinInfo.joinType == 1) {
					base = base.join(joinInfo.targetEntityPath);
				} else if (joinInfo.joinType == 2) {
					base = base.join(joinInfo.targetEntityPath, joinInfo.alias);
				} else if (joinInfo.joinType == 3) {
					base = base.join(joinInfo.targetCollection);
				} else if (joinInfo.joinType == 4) {
					base = base.join(joinInfo.targetCollection, joinInfo.alias);
				} else if (joinInfo.joinType == 5) {
					base = base.join(joinInfo.targetMap);
				} else if (joinInfo.joinType == 6) {
					base = base.join(joinInfo.targetMap, joinInfo.alias);
				}

				if (joinInfo.isFetchJoin) {
					base = base.fetchJoin();
				}
			}

			base = base.on(on.toArray(Predicate[]::new));
		}
		return base;
	}

	public static boolean when(final boolean cond) {
		return cond;
	}

	public static List<Predicate> on(final Predicate... condition) {

		return Arrays.asList(condition);
	}

}
