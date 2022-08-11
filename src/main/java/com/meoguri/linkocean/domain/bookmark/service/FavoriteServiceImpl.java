package com.meoguri.linkocean.domain.bookmark.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.meoguri.linkocean.domain.bookmark.entity.Bookmark;
import com.meoguri.linkocean.domain.bookmark.entity.Favorite;
import com.meoguri.linkocean.domain.bookmark.persistence.FavoriteRepository;
import com.meoguri.linkocean.domain.bookmark.persistence.FindBookmarkByIdQuery;
import com.meoguri.linkocean.domain.profile.entity.Profile;
import com.meoguri.linkocean.domain.profile.persistence.FindProfileByUserIdQuery;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class FavoriteServiceImpl implements FavoriteService {

	private final FavoriteRepository favoriteRepository;

	private final FindProfileByUserIdQuery findProfileByUserIdQuery;
	private final FindBookmarkByIdQuery findBookmarkByIdQuery;

	@Override
	public void favorite(final long userId, final long bookmarkId) {

		final Bookmark bookmark = findBookmarkByIdQuery.findById(bookmarkId);
		final Profile owner = findProfileByUserIdQuery.findByUserId(userId);

		favoriteRepository.save(new Favorite(bookmark, owner));
	}

	@Override
	public void unfavorite(final long userId, final long bookmarkId) {

		final Bookmark bookmark = findBookmarkByIdQuery.findById(bookmarkId);
		final Profile owner = findProfileByUserIdQuery.findByUserId(userId);

		favoriteRepository.deleteByOwnerAndBookmark(owner, bookmark);
	}
}
