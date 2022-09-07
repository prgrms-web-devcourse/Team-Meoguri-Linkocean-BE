package com.meoguri.linkocean.util.querydsl;

import static java.util.Collections.*;
import static java.util.stream.Collectors.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.LongSupplier;
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
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.querydsl.jpa.sql.JPASQLQuery;
import com.querydsl.sql.MySQLTemplates;
import com.querydsl.sql.SQLTemplates;

/**
 * Querydsl 4.x 버전에 맞춘 Querydsl 지원 라이브러리
 * @see org.springframework.data.jpa.repository.support.QuerydslRepositorySupport
 */
@Repository
public abstract class Querydsl4RepositorySupport {

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

	protected <T> JPAQuery<T> applyDynamicJoin(
		JPAQuery<T> contentQuery,
		final JoinInfoBuilder.JoinIf joinIf
	) {
		return applyDynamicJoin(contentQuery, joinIfs(joinIf));
	}

	protected <T> JPAQuery<T> applyDynamicJoin(
		JPAQuery<T> contentQuery,
		final List<JoinInfoBuilder.JoinIf> joinIfs
	) {
		/* dynamic join 적용 */
		for (JoinInfoBuilder.JoinIf joinIf : joinIfs) {
			contentQuery = joinIf.apply(contentQuery);
		}
		return contentQuery;
	}

	private <T> JPAQuery<T> applyWhere(JPAQuery<T> contentQuery, final List<Predicate> where) {
		final Predicate[] whereArray = where.toArray(new Predicate[0]);
		contentQuery = contentQuery.where(whereArray);
		return contentQuery;
	}

	/* 동적 쿼리에 대한 페이징 적용
	- group by, having 등의 문제가 될 수 있는 쿼리를 사용하지 않기 때문에 JPAQuery.fetchCount 사용 */
	@SuppressWarnings("deprecation")
	protected <T> Page<T> applyDynamicPagination(
		final Pageable pageable,
		JPAQuery<T> contentQuery,
		final List<JoinInfoBuilder.JoinIf> joinIfs,
		final List<Predicate> where
	) {
		JPAQuery<T> countQuery = contentQuery.clone(entityManager);

		/* 동적 쿼리 적용 해서 content 조회 */
		contentQuery = applyDynamicJoin(contentQuery, joinIfs);
		contentQuery = applyWhere(contentQuery, where);
		contentQuery = applyPagination(pageable, contentQuery);

		/* content query 에는 where 만 적용 */
		countQuery = applyWhere(countQuery, where);

		/* 전체 카운트를 포함한 페이지 반환 */
		List<T> content = contentQuery.fetch();
		final LongSupplier totalSupplier = countQuery::fetchCount;
		return PageableExecutionUtils.getPage(content, pageable, totalSupplier);
	}

	protected <T> JPAQuery<T> applyPagination(final Pageable pageable, final JPAQuery<T> contentQuery) {
		return (JPAQuery<T>)querydsl.applyPagination(pageable, contentQuery);
	}

	/* 무한 스크롤 전용 슬라이싱 */
	protected <T> Slice<T> applyDynamicSlicing(
		final Pageable pageable,
		JPAQuery<T> contentQuery,
		final List<Predicate> where
	) {
		/* content query 에 where, slice 적용 적용 */
		contentQuery = applyWhere(contentQuery, where);
		contentQuery = applySlicing(pageable, contentQuery);

		/* hasNext 결과를 포함한 슬라이스 반환 */
		List<T> content = contentQuery.fetch();
		boolean hasNext = isHasNext(pageable, content);
		return new SliceImpl<>(content, pageable, hasNext);
	}

	protected <T> JPAQuery<T> applySlicing(final Pageable pageable, JPAQuery<T> contentQuery) {
		contentQuery = contentQuery
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize() + 1);
		return (JPAQuery<T>)querydsl.applySorting(pageable.getSort(), contentQuery);
	}

	private <T> boolean isHasNext(final Pageable pageable, final List<T> content) {
		boolean hasNext = false;
		if (content.size() > pageable.getPageSize()) {
			content.remove(pageable.getPageSize());
			hasNext = true;
		}
		return hasNext;
	}

	protected <T> JPAQuery<T> select(final Expression<T> expr) {
		return queryFactory.select(expr);
	}

	protected <T> JPAQuery<T> selectFrom(final EntityPath<T> from) {
		return queryFactory.selectFrom(from);
	}

	protected JPAQuery<Integer> selectOne() {
		return queryFactory.selectOne();
	}

	public List<JoinInfoBuilder.JoinIf> joinIfs(JoinInfoBuilder.JoinIf... joinIfs) {
		return Arrays.stream(joinIfs).collect(toList());
	}

	protected JoinInfoBuilder.JoinIf joinIf(
		final boolean expression,
		final Supplier<JoinInfoBuilder> joinInfoBuilder
	) {
		return new JoinInfoBuilder.JoinIf(expression, joinInfoBuilder);
	}

	/* 동적 where 절을 지원하기 위한 유틸리티 메서드 */
	@SafeVarargs
	protected final List<Predicate> where(final List<Predicate> always, final List<Predicate>... whereIfs) {
		final List<Predicate> wheres = new ArrayList<>(always);
		Arrays.stream(whereIfs).forEachOrdered(wheres::addAll);
		return wheres;
	}

	protected static List<Predicate> always(final Predicate... where) {
		return Arrays.stream(where).collect(toList());
	}

	@SafeVarargs
	protected static List<Predicate> whereIf(final boolean expression, final Supplier<Predicate>... where) {
		return expression ? Arrays.stream(where).map(Supplier::get).collect(toList()) : emptyList();
	}

	protected static BooleanBuilder nullSafeBuilder(final Supplier<BooleanExpression> cond) {
		try {
			return new BooleanBuilder(cond.get());
		} catch (IllegalArgumentException | NullPointerException e) {
			return new BooleanBuilder();
		}
	}

}

