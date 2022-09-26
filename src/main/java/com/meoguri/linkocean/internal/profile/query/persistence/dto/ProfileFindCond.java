package com.meoguri.linkocean.internal.profile.query.persistence.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 프로필 목록 조회 조건
 * - 사용자 팔로우 목록 조회
 * - 사용자 팔로이 목록 조회
 * - 다른 사용자 이름 검색
 */
@Getter
@Builder
@RequiredArgsConstructor
public class ProfileFindCond {

	/* 팔로우, 팔로이 목록 조회에서 현재 사용자의 프로필 아이디 */
	private final Long profileId;

	/* 팔로워 목록 조회 */
	private final boolean follower;

	/* 팔로이 목록 조회 */
	private final boolean followee;

	/* 검색하고 싶은 사용자의 username */
	private final String username;
}
