package com.meoguri.linkocean.domain.bookmark.service.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 리액션을 추가/취소 하기 위한 커맨드
 */
@Getter
@RequiredArgsConstructor
public class ReactionCommand {

	/* 리액션을 추가/취소 한 사용자의 아이디 */
	private final long userId;

	/* 리액션을 추가/취소 한 북마크의 아이디 */
	private final long bookmarkId;

	/* 리액션종류 */
	private final String reactionType;

}
