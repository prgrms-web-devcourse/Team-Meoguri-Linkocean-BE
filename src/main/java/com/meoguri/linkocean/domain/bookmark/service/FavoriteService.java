package com.meoguri.linkocean.domain.bookmark.service;

public interface FavoriteService {

	/* 즐겨찾기 */
	void favorite(long userId, long bookmarkId);

	/* 즐겨찾기 취소 */
	void unfavorite(long userId, long bookmarkId);
}
