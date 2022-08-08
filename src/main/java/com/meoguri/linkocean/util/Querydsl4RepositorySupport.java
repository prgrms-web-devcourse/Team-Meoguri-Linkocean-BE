package com.meoguri.linkocean.util;

import static com.meoguri.linkocean.domain.bookmark.entity.QBookmark.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.JpaEntityInformationSupport;
import org.springframework.data.jpa.repository.support.Querydsl;
import org.springframework.data.querydsl.QPageRequest;
import org.springframework.data.querydsl.SimpleEntityPathResolver;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.EntityPath;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;

/**
 * Querydsl 4.x 버전에 맞춘 Querydsl 지원 라이브러리
 *
 * @author Younghan Kim - 인프런 김영한 - 실전! Querydsl! 강의에서 소개한 코드를 활용 하였습니다
 * @see org.springframework.data.jpa.repository.support.QuerydslRepositorySupport
 */
@Repository
public abstract class Querydsl4RepositorySupport {
	private final Class<?> domainClass;
	private Querydsl querydsl;
	private EntityManager entityManager;
	private JPAQueryFactory queryFactory;

	public Querydsl4RepositorySupport(Class<?> domainClass) {
		Assert.notNull(domainClass, "Domain class must not be null!");
		this.domainClass = domainClass;
	}

	@Autowired
	public void setEntityManager(EntityManager entityManager) {
		Assert.notNull(entityManager, "EntityManager must not be null!");
		JpaEntityInformation<?, ?> entityInformation =
			JpaEntityInformationSupport.getEntityInformation(domainClass, entityManager);
		SimpleEntityPathResolver resolver = SimpleEntityPathResolver.INSTANCE;
		EntityPath<?> path = resolver.createPath(entityInformation.getJavaType());
		this.entityManager = entityManager;
		this.querydsl = new Querydsl(entityManager, new
			PathBuilder<>(path.getType(), path.getMetadata()));
		this.queryFactory = new JPAQueryFactory(entityManager);
	}

	@PostConstruct
	public void validate() {
		Assert.notNull(entityManager, "EntityManager must not be null!");
		Assert.notNull(querydsl, "Querydsl must not be null!");
		Assert.notNull(queryFactory, "QueryFactory must not be null!");
	}

	protected JPAQueryFactory getQueryFactory() {
		return queryFactory;
	}

	protected Querydsl getQuerydsl() {
		return querydsl;
	}

	protected EntityManager getEntityManager() {
		return entityManager;

	}

	protected <T> JPAQuery<T> select(Expression<T> expr) {
		return getQueryFactory().select(expr);
	}

	protected <T> JPAQuery<T> selectFrom(EntityPath<T> from) {
		return getQueryFactory().selectFrom(from);
	}

	protected <T> Page<T> applyPagination(
		Pageable pageable,
		Function<JPAQueryFactory, JPAQuery<T>> contentQuery,
		Consumer<T> lazyLoader
	) {
		return applyPagination(pageable, contentQuery, lazyLoader, contentQuery);
	}

	protected <T> Page<T> applyPagination(
		Pageable pageable,
		Function<JPAQueryFactory, JPAQuery<T>> contentQuery,
		Consumer<T> lazyLoader,
		Function<JPAQueryFactory, JPAQuery<T>> countQuery
	) {
		pageable = convertBookmarkSort(pageable);
		JPAQuery<T> jpaContentQuery = contentQuery.apply(getQueryFactory());
		List<T> content = getQuerydsl().applyPagination(pageable, jpaContentQuery).fetch();
		content.forEach(lazyLoader);
		JPAQuery<T> countResult = countQuery.apply(getQueryFactory());
		return PageableExecutionUtils.getPage(content, pageable, () -> countResult.stream().count());
	}

	private Pageable convertBookmarkSort(Pageable pageable) {
		return QPageRequest.of(
			pageable.getPageNumber(),
			pageable.getPageSize(),
			toBookmarkOrderSpecifiers(pageable).toArray(OrderSpecifier[]::new)
		);
	}

	private List<OrderSpecifier<?>> toBookmarkOrderSpecifiers(Pageable pageable) {
		final Order direction = Order.DESC;
		final List<OrderSpecifier<?>> result = new ArrayList<>();

		for (Sort.Order order : pageable.getSort()) {
			switch (order.getProperty()) {
				case "like":
					result.add(new OrderSpecifier<>(direction, bookmark.likeCount));
					break;
				case "upload":
					result.add(new OrderSpecifier<>(direction, bookmark.createdAt));
					break;
			}
		}
		return result;
	}

	/**
	 * 동적 where 절을 지원하기 위한 유틸리티 메서드
	 */
	protected static BooleanBuilder nullSafeBuilder(final Supplier<BooleanExpression> cond) {
		try {
			return new BooleanBuilder(cond.get());
		} catch (IllegalArgumentException | NullPointerException e) {
			return new BooleanBuilder();
		}
	}

	/**
	 * 동적 join 을 지원하기 위한 유틸리티 메서드
	 */
	protected static <T> JPQLQuery<T> joinIf(
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

	protected static boolean when(final boolean cond) {
		return cond;
	}

	protected static List<Predicate> on(final Predicate... condition) {

		return Arrays.asList(condition);
	}

}

