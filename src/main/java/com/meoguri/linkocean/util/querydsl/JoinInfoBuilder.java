package com.meoguri.linkocean.util.querydsl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.querydsl.core.types.CollectionExpression;
import com.querydsl.core.types.EntityPath;
import com.querydsl.core.types.MapExpression;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.Predicate;

/**
 * 동적 Join 을 제공하기 위한 클래스
 * JoinInfo 리스트를 Builder 패턴으로 제공한다
 */
public class JoinInfoBuilder {

	// join 목록
	List<Join> joinList = new ArrayList<>();


	private static JoinInfoBuilder builder() {
		return new JoinInfoBuilder();
	}

	/**
	 * join 메서드 이름을 static, non-static 을 모두 가져가기 위해 내부 클래스를 두었습니다
	 */
	public static class Initializer {

		public static JoinInfoBuilder join(EntityPath target) {
			return builder().join(target);
		}

		public static JoinInfoBuilder join(EntityPath target, Path alias) {
			return builder().join(target, alias);
		}

		public static JoinInfoBuilder join(CollectionExpression target) {
			return builder().join(target);
		}

		public static JoinInfoBuilder join(CollectionExpression target, Path alias) {
			return builder().join(target, alias);
		}

		public static JoinInfoBuilder join(MapExpression target) {
			return builder().join(target);
		}

		public static JoinInfoBuilder join(MapExpression target, Path alias) {
			return builder().join(target, alias);
		}
	}

	public JoinInfoBuilder join(EntityPath target) {
		this.joinList.add(new Join(target));
		return this;
	}

	public JoinInfoBuilder join(EntityPath target, Path alias) {
		this.joinList.add(new Join(target, alias));
		return this;
	}

	public JoinInfoBuilder join(CollectionExpression target) {
		this.joinList.add(new Join(target));
		return this;
	}

	public JoinInfoBuilder join(CollectionExpression target, Path alias) {
		this.joinList.add(new Join(target, alias));
		return this;
	}

	public JoinInfoBuilder join(MapExpression target) {
		this.joinList.add(new Join(target));
		return this;
	}

	public JoinInfoBuilder join(MapExpression target, Path alias) {
		this.joinList.add(new Join(target, alias));
		return this;
	}

	public JoinInfoBuilder fetchJoin() {
		this.joinList.get(this.joinList.size() - 1).fetchJoin = true;
		return this;
	}

	public JoinInfoBuilder on(final Predicate... condition) {
		this.joinList.get(this.joinList.size() - 1).on = true;
		this.joinList.get(this.joinList.size() - 1).condition = Arrays.asList(condition);
		return this;
	}

	JoinInfoBuilder build() {
		return this;
	}

}
