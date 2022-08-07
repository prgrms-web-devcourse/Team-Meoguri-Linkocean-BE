package com.meoguri.linkocean.util;

import java.util.ArrayList;
import java.util.List;

import com.querydsl.core.types.CollectionExpression;
import com.querydsl.core.types.EntityPath;
import com.querydsl.core.types.MapExpression;
import com.querydsl.core.types.Path;

/**
 * 동적 Join 을 제공하기 위한 클래스
 * JoinInfo 리스트를 Builder 패턴으로 제공한다
 */
public class JoinInfoListBuilder {

	private List<JoinInfo> joinInfoList = new ArrayList<>();

	private static JoinInfoListBuilder builder() {

		return new JoinInfoListBuilder();
	}

	/**
	 * join 메서드 이름을 static, non-static 을 모두 가져가기 위해 내부 클래스를 두었습니다
	 */
	public static class JoinInfoListBuilderInitializer {

		public static JoinInfoListBuilder join(EntityPath target) {
			return builder().join(target);
		}

		public static JoinInfoListBuilder join(EntityPath target, Path alias) {
			return builder().join(target, alias);
		}

		public static JoinInfoListBuilder join(CollectionExpression target) {
			return builder().join(target);
		}

		public static JoinInfoListBuilder join(CollectionExpression target, Path alias) {
			return builder().join(target, alias);
		}

		public static JoinInfoListBuilder join(MapExpression target) {
			return builder().join(target);
		}

		public static JoinInfoListBuilder join(MapExpression target, Path alias) {
			return builder().join(target, alias);
		}
	}

	public JoinInfoListBuilder join(EntityPath target) {
		this.joinInfoList.add(new JoinInfo(target));
		return this;
	}

	public JoinInfoListBuilder join(EntityPath target, Path alias) {
		this.joinInfoList.add(new JoinInfo(target, alias));
		return this;
	}

	public JoinInfoListBuilder join(CollectionExpression target) {
		this.joinInfoList.add(new JoinInfo(target));
		return this;
	}

	public JoinInfoListBuilder join(CollectionExpression target, Path alias) {
		this.joinInfoList.add(new JoinInfo(target, alias));
		return this;
	}

	public JoinInfoListBuilder join(MapExpression target) {
		this.joinInfoList.add(new JoinInfo(target));
		return this;
	}

	public JoinInfoListBuilder join(MapExpression target, Path alias) {
		this.joinInfoList.add(new JoinInfo(target, alias));
		return this;
	}

	public JoinInfoListBuilder fetchJoin() {
		this.joinInfoList.get(this.joinInfoList.size() - 1).isFetchJoin = true;
		return this;
	}

	List<JoinInfo> build() {
		return joinInfoList;
	}
}
