package com.meoguri.linkocean.domain.bookmark.service.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 리액션을 추가/취소 하기 위한 커맨드
 */
@Getter
@RequiredArgsConstructor
public final class ReactionCommand {

	/* 리액션을 추가/취소 한 프로필의 아이디 */
	private final long profileId;

	/* 리액션을 추가/취소 한 북마크의 아이디 */
	private final long bookmarkId;

	/* 리액션종류 */
	private final String reactionType;

}
