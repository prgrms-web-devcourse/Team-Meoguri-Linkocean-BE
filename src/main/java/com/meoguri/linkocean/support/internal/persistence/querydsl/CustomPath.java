package com.meoguri.linkocean.support.internal.persistence.querydsl;

import static com.meoguri.linkocean.internal.bookmark.entity.QBookmark.*;

import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.core.types.dsl.StringPath;
import com.querydsl.sql.RelationalPathBase;

import lombok.NoArgsConstructor;

/* Entity 가 아닌 대상으로 쿼리 하기 위한 Custom Path */
@NoArgsConstructor
public final class CustomPath {

	/* favorite f */
	public static RelationalPathBase<Object> favorite = new RelationalPathBase<>(Object.class, "f", "linkocean",
		"favorite");

	/* f.owner_id */
	public static NumberPath<Long> profileId = Expressions.numberPath(Long.class, favorite, "owner_id");

	/* f.bookmark_id */
	public static NumberPath<Long> bookmarkId = Expressions.numberPath(Long.class, favorite, "bookmark_id");

	/* bookmark_tag bt */
	public static RelationalPathBase<Object> bookmark_tag = new RelationalPathBase<>(Object.class, "bt", "linkocean",
		"bookmark_tag");

	/* bt.bookmark_id */
	public static NumberPath<Long> bt_bookmarkId = Expressions.numberPath(Long.class, bookmark_tag, "bookmark_id");

	/* bt.tag_id */
	public static NumberPath<Long> bt_tagId = Expressions.numberPath(Long.class, bookmark_tag, "tag_id");

	/* bookmark.profile_id */
	public static NumberPath<Long> b_profileId = Expressions.numberPath(Long.class, bookmark, "profile_id");

	/* bookmark.status */
	public static StringPath b_status = Expressions.stringPath(bookmark, "status");
}
