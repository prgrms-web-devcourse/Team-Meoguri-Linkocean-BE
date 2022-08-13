package com.meoguri.linkocean.domain.bookmark.service.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 리액션을 등록/취소/변경 하기 위한 커맨드
 */
@Getter
@RequiredArgsConstructor
public final class ReactionCommand {

	/* 사용자의 프로필 아이디 */
	private final long profileId;

	/* 북마크의 아이디 */
	private final long bookmarkId;

	/* 리액션 종류 */
	private final String reactionType;

}
