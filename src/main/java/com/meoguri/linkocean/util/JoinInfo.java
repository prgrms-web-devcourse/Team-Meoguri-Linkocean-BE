package com.meoguri.linkocean.util;

import com.querydsl.core.types.CollectionExpression;
import com.querydsl.core.types.EntityPath;
import com.querydsl.core.types.MapExpression;
import com.querydsl.core.types.Path;

public class JoinInfo {

	EntityPath targetEntityPath;

	CollectionExpression targetCollection;

	MapExpression targetMap;

	Path alias;

	boolean isFetchJoin;

	int joinType;

	public JoinInfo(EntityPath target) {
		this.targetEntityPath = target;
		this.joinType = 1;
	}

	public JoinInfo(EntityPath target, Path alias) {
		this.targetEntityPath = target;
		this.alias = alias;
		this.joinType = 2;
	}

	public JoinInfo(CollectionExpression target) {
		this.targetCollection = target;
		this.joinType = 3;
	}

	public JoinInfo(CollectionExpression target, Path alias) {
		this.targetCollection = target;
		this.alias = alias;
		this.joinType = 4;
	}

	public JoinInfo(MapExpression target) {
		this.targetMap = target;
		this.joinType = 5;
	}

	public JoinInfo(MapExpression target, Path alias) {
		this.targetMap = target;
		this.alias = alias;
		this.joinType = 6;
	}

}
