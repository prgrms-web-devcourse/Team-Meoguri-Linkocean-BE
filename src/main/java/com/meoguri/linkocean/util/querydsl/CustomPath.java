package com.meoguri.linkocean.util.querydsl;

import com.meoguri.linkocean.domain.bookmark.entity.vo.ReactionType;
import com.querydsl.core.types.dsl.EnumPath;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberPath;
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

	/* reaction r */
	public static RelationalPathBase<Object> reaction = new RelationalPathBase<>(Object.class, "r", "linkocean",
		"reaction");

	/* r.profile_id */
	public static NumberPath<Long> r_profileId = Expressions.numberPath(Long.class, reaction, "profile_id");

	/* r.bookmark_id */
	public static NumberPath<Long> r_bookmarkId = Expressions.numberPath(Long.class, reaction, "bookmark_id");

	/* r.type */
	public static EnumPath<ReactionType> r_type = Expressions.enumPath(ReactionType.class, reaction, "type");

}
