package com.meoguri.linkocean.domain.bookmark.service.dto;

import lombok.Getter;

/**
 * 리액션을 추가/취소 하기 위한 커맨드
 */
@Getter
public class ReactionCommand {

	/* 리액션을 추가/취소 한 사용자의 아이디 */
	private final long userId;

	/* 리액션을 추가/취소 한 북마크의 아이디 */
	private final long bookmarkId;

	/* 리액션종류 */
	private final String reactionType;

	public ReactionCommand(final long userId, final long bookmarkId, final String reactionType) {

		this.userId = userId;
		this.bookmarkId = bookmarkId;
		this.reactionType = reactionType.toUpperCase();
	}
}
