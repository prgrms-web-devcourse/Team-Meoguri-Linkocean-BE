package com.meoguri.linkocean.domain.profile.service.dto;

import java.util.Optional;

import com.meoguri.linkocean.exception.LinkoceanRuntimeException;

import lombok.Getter;

@Getter
public final class ProfileSearchCond {

	private static final int DEFAULT_PAGE = 1;
	private static final int DEFAULT_SIZE = 8;

	private final long userId;
	private final int page;
	private final int size;
	private final String username;

	public ProfileSearchCond(final long userId, final Integer page, final Integer size, final String username) {

		this.userId = userId;
		this.page = Optional.ofNullable(page).orElse(DEFAULT_PAGE);
		this.size = Optional.ofNullable(size).orElse(DEFAULT_SIZE);
		this.username = username;
	}

	// TODO - Unused Controller Dto 구현시 사용할 것

	/**
	 * 프로필 검색 페이지의 탭
	 * 자신의 팔로워 혹은 팔로이 목록을 선택하여 검색한다.
	 */
	private enum SearchTab {
		FOLLOWER,
		FOLLOWEE;

		String getName() {
			return name().toLowerCase();
		}

		static SearchTab of(String arg) {
			try {
				return SearchTab.valueOf(arg.toUpperCase());
			} catch (IllegalArgumentException e) {
				throw new LinkoceanRuntimeException();
			}
		}
	}
}
