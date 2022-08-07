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
	public static <T, P> JPQLQuery<T> joinIf(JPQLQuery<T> base,
		final List<JoinInfo> joinInfo, final List<Predicate> on, final Boolean when) {

		if (when) {
			for (JoinInfo pJoinInfo : joinInfo) {

				if (pJoinInfo.joinType == 1) {
					base = base.join(pJoinInfo.targetEntityPath);
				} else if (pJoinInfo.joinType == 2) {
					base = base.join(pJoinInfo.targetEntityPath, pJoinInfo.alias);
				} else if (pJoinInfo.joinType == 3) {
					base = base.join(pJoinInfo.targetCollection);
				} else if (pJoinInfo.joinType == 4) {
					base = base.join(pJoinInfo.targetCollection, pJoinInfo.alias);
				} else if (pJoinInfo.joinType == 5) {
					base = base.join(pJoinInfo.targetMap);
				} else if (pJoinInfo.joinType == 6) {
					base = base.join(pJoinInfo.targetMap, pJoinInfo.alias);
				}

				if (pJoinInfo.isFetchJoin) {
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
