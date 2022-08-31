package com.meoguri.linkocean.domain.profile.command.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.meoguri.linkocean.domain.bookmark.entity.Bookmark;
import com.meoguri.linkocean.domain.bookmark.persistence.FindBookmarkByIdQuery;
import com.meoguri.linkocean.domain.profile.command.persistence.FindProfileByIdQuery;
import com.meoguri.linkocean.domain.profile.entity.Profile;

import lombok.RequiredArgsConstructor;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class FavoriteServiceImpl implements FavoriteService {

	private final FindProfileByIdQuery findProfileByIdQuery;
	private final FindBookmarkByIdQuery findBookmarkByIdQuery;

	@Transactional
	@Override
	public void favorite(final long profileId, final long bookmarkId) {
		final Profile profile = findProfileByIdQuery.findProfileFetchFavoriteById(profileId);
		final Bookmark bookmark = findBookmarkByIdQuery.findById(bookmarkId);

		profile.favorite(bookmark);
	}

	@Transactional
	@Override
	public void unfavorite(final long profileId, final long bookmarkId) {
		final Profile profile = findProfileByIdQuery.findProfileFetchFavoriteById(profileId);
		final Bookmark bookmark = findBookmarkByIdQuery.findById(bookmarkId);

		profile.unfavorite(bookmark);
	}
}
