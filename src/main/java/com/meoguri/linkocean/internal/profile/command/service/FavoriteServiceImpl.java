package com.meoguri.linkocean.internal.profile.command.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.meoguri.linkocean.internal.bookmark.entity.Bookmark;
import com.meoguri.linkocean.internal.bookmark.persistence.FindBookmarkByIdRepository;
import com.meoguri.linkocean.internal.profile.entity.Profile;
import com.meoguri.linkocean.internal.profile.query.persistence.FindProfileByIdRepository;

import lombok.RequiredArgsConstructor;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class FavoriteServiceImpl implements FavoriteService {

	private final FindProfileByIdRepository findProfileByIdRepository;
	private final FindBookmarkByIdRepository findBookmarkByIdRepository;

	@Transactional
	@Override
	public void favorite(final long profileId, final long bookmarkId) {
		final Profile profile = findProfileByIdRepository.findProfileFetchFavoriteIdsById(profileId);
		final Bookmark bookmark = findBookmarkByIdRepository.findById(bookmarkId);

		profile.favorite(bookmark);
	}

	@Transactional
	@Override
	public void unfavorite(final long profileId, final long bookmarkId) {
		final Profile profile = findProfileByIdRepository.findProfileFetchFavoriteIdsById(profileId);
		final Bookmark bookmark = findBookmarkByIdRepository.findById(bookmarkId);

		profile.unfavorite(bookmark);
	}
}
