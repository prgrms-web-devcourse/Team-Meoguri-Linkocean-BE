package com.meoguri.linkocean.util;

import java.util.ArrayList;
import java.util.List;

import com.querydsl.core.types.CollectionExpression;
import com.querydsl.core.types.EntityPath;
import com.querydsl.core.types.MapExpression;
import com.querydsl.core.types.Path;

public class JoinInfoListBuilder {

	private List<JoinInfo> joinInfoList = new ArrayList<>();

	public static JoinInfoListBuilder builder() {

		return new JoinInfoListBuilder();
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

	public List<JoinInfo> build() {
		return joinInfoList;
	}
}
