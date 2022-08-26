package com.meoguri.linkocean.domain.profile.command.service;

public interface FavoriteService {

	/* 즐겨찾기 */
	void favorite(long profileId, long bookmarkId);

	/* 즐겨찾기 취소 */
	void unfavorite(long profileId, long bookmarkId);
}
