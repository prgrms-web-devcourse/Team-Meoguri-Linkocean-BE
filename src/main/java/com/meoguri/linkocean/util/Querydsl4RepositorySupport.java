package com.meoguri.linkocean.util;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.JpaEntityInformationSupport;
import org.springframework.data.jpa.repository.support.Querydsl;
import org.springframework.data.querydsl.SimpleEntityPathResolver;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.EntityPath;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.querydsl.jpa.sql.JPASQLQuery;
import com.querydsl.sql.MySQLTemplates;
import com.querydsl.sql.RelationalPathBase;
import com.querydsl.sql.SQLTemplates;

/**
 * Querydsl 4.x 버전에 맞춘 Querydsl 지원 라이브러리
 *
 * @author Younghan Kim - 인프런 김영한 - 실전! Querydsl! 강의에서 소개한 코드를 활용 하였습니다
 * @see org.springframework.data.jpa.repository.support.QuerydslRepositorySupport
 */
@Repository
public abstract class Querydsl4RepositorySupport {

	protected static RelationalPathBase<Object> favorite = new RelationalPathBase<>(Object.class, "f", "linkocean",
		"favorite");
	protected static NumberPath<Long> ownerId = Expressions.numberPath(Long.class, favorite, "owner_id");

	protected static NumberPath<Long> bookmarkId = Expressions.numberPath(Long.class, favorite, "bookmark_id");

	private final Class<?> domainClass;

	protected Querydsl querydsl;
	protected EntityManager entityManager;
	protected JPAQueryFactory queryFactory;

	private SQLTemplates sqlTemplates;

	public JPASQLQuery<?> getJpasqlQuery() {
		return new JPASQLQuery<>(entityManager, sqlTemplates);
	}

	public Querydsl4RepositorySupport(final Class<?> domainClass) {
		Assert.notNull(domainClass, "Domain class must not be null!");
		this.domainClass = domainClass;
	}

	@Autowired
	public void setEntityManager(final EntityManager entityManager) {
		Assert.notNull(entityManager, "EntityManager must not be null!");

		final JpaEntityInformation<?, ?> entityInformation =
			JpaEntityInformationSupport.getEntityInformation(domainClass, entityManager);
		final SimpleEntityPathResolver resolver = SimpleEntityPathResolver.INSTANCE;
		final EntityPath<?> path = resolver.createPath(entityInformation.getJavaType());

		this.entityManager = entityManager;
		this.querydsl = new Querydsl(entityManager, new PathBuilder<>(path.getType(), path.getMetadata()));
		this.queryFactory = new JPAQueryFactory(entityManager);
		this.sqlTemplates = new MySQLTemplates();
	}

	@PostConstruct
	public void validate() {
		Assert.notNull(entityManager, "EntityManager must not be null!");
		Assert.notNull(querydsl, "Querydsl must not be null!");
		Assert.notNull(queryFactory, "QueryFactory must not be null!");
	}

	protected <T> JPAQuery<T> select(final Expression<T> expr) {
		return queryFactory.select(expr);
	}

	protected <T> JPAQuery<T> selectFrom(final EntityPath<T> from) {
		return queryFactory.selectFrom(from);
	}

	protected <T> Page<T> applyPagination(
		final Pageable pageable,
		final JPAQuery<T> jpaContentQuery,
		final Consumer<T> lazyLoader
	) {
		return applyPagination(pageable, jpaContentQuery, lazyLoader, jpaContentQuery);
	}

	protected <T> Page<T> applyPagination(
		final Pageable pageable,
		final JPAQuery<T> jpaContentQuery,
		final Consumer<T> lazyLoader,
		final JPAQuery<T> jpaCountQuery
	) {
		List<T> content = querydsl.applyPagination(pageable, jpaContentQuery).fetch();
		content.forEach(lazyLoader);
		return PageableExecutionUtils.getPage(content, pageable, jpaCountQuery::fetchCount);
	}

	/* 무한 스크롤 전용 슬라이싱 */
	protected <T> Slice<T> applySlicing(
		final Pageable pageable,
		final JPAQuery<T> jpaContentQuery
	) {
		final List<T> content = jpaContentQuery
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize() + 1)
			.fetch();

		boolean hasNext = false;
		if (content.size() > pageable.getPageSize()) {
			content.remove(pageable.getPageSize());
			hasNext = true;
		}

		return new SliceImpl<>(content, pageable, hasNext);
	}

	/* 동적 where 절을 지원하기 위한 유틸리티 메서드 */
	protected static BooleanBuilder nullSafeBuilder(final Supplier<BooleanExpression> cond) {
		try {
			return new BooleanBuilder(cond.get());
		} catch (IllegalArgumentException | NullPointerException e) {
			return new BooleanBuilder();
		}
	}

	/* 동적 join 을 지원하기 위한 유틸리티 메서드 */
	protected static <T> JPQLQuery<T> joinIf(
		final boolean expression,
		JPQLQuery<T> base,
		final Supplier<JoinInfoBuilder> joinInfoBuilder
	) {
		if (expression) {
			final JoinInfoBuilder joinInfo = joinInfoBuilder.get().build();

			for (Join join : joinInfo.joinList) {
				if (join.joinType == 1) {
					base = base.join(join.targetEntityPath);
				} else if (join.joinType == 2) {
					base = base.join(join.targetEntityPath, join.alias);
				} else if (join.joinType == 3) {
					base = base.join(join.targetCollection);
				} else if (join.joinType == 4) {
					base = base.join(join.targetCollection, join.alias);
				} else if (join.joinType == 5) {
					base = base.join(join.targetMap);
				} else if (join.joinType == 6) {
					base = base.join(join.targetMap, join.alias);
				}

				if (join.fetchJoin) {
					base = base.fetchJoin();
				}

				if (join.on) {
					base = base.on(join.condition.toArray(Predicate[]::new));
				}
			}
		}
		return base;
	}

}

