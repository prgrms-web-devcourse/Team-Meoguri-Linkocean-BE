package com.meoguri.linkocean.domain.bookmark.service;

public interface FavoriteService {

	void favorite(long userId, long bookmarkId);

	void unfavorite(long userId, long bookmarkId);
}
