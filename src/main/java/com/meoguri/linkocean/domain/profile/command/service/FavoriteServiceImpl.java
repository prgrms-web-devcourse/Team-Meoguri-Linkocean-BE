package com.meoguri.linkocean.domain.profile.command.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.meoguri.linkocean.domain.bookmark.entity.Bookmark;
import com.meoguri.linkocean.domain.bookmark.persistence.FindBookmarkByIdQuery;
import com.meoguri.linkocean.domain.profile.entity.Profile;
import com.meoguri.linkocean.domain.profile.query.persistence.FindProfileByIdRepository;

import lombok.RequiredArgsConstructor;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class FavoriteServiceImpl implements FavoriteService {

	private final FindProfileByIdRepository findProfileByIdRepository;
	private final FindBookmarkByIdQuery findBookmarkByIdQuery;

	@Transactional
	@Override
	public void favorite(final long profileId, final long bookmarkId) {
		final Profile profile = findProfileByIdRepository.findProfileFetchFavoriteIdsById(profileId);
		final Bookmark bookmark = findBookmarkByIdQuery.findById(bookmarkId);

		profile.favorite(bookmark);
	}

	@Transactional
	@Override
	public void unfavorite(final long profileId, final long bookmarkId) {
		final Profile profile = findProfileByIdRepository.findProfileFetchFavoriteIdsById(profileId);
		final Bookmark bookmark = findBookmarkByIdQuery.findById(bookmarkId);

		profile.unfavorite(bookmark);
	}
}
