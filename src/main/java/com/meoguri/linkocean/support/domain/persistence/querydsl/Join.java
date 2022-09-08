package com.meoguri.linkocean.support.domain.persistence.querydsl;

import java.util.List;

import com.querydsl.core.types.CollectionExpression;
import com.querydsl.core.types.EntityPath;
import com.querydsl.core.types.MapExpression;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.Predicate;

/**
 * JPAQueryBase의 join의 파라미터 정보를 담는 클래스
 * @see com.querydsl.jpa.JPAQueryBase
 */
public class Join {

	EntityPath targetEntityPath;

	CollectionExpression targetCollection;

	MapExpression targetMap;

	Path alias;

	/* 페치 조인 여부 */
	boolean fetchJoin;

	/* on 절 사용 여부 */
	boolean on;

	/* on 절에 사용되는 Predicate 목록 */
	List<Predicate> condition;

	int joinType;

	public Join(EntityPath target) {
		this.targetEntityPath = target;
		this.joinType = 1;
	}

	public Join(EntityPath target, Path alias) {
		this.targetEntityPath = target;
		this.alias = alias;
		this.joinType = 2;
	}

	public Join(CollectionExpression target) {
		this.targetCollection = target;
		this.joinType = 3;
	}

	public Join(CollectionExpression target, Path alias) {
		this.targetCollection = target;
		this.alias = alias;
		this.joinType = 4;
	}

	public Join(MapExpression target) {
		this.targetMap = target;
		this.joinType = 5;
	}

	public Join(MapExpression target, Path alias) {
		this.targetMap = target;
		this.alias = alias;
		this.joinType = 6;
	}

}
