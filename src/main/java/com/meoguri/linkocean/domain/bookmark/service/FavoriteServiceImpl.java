package com.meoguri.linkocean.domain.bookmark.service;

import static com.meoguri.linkocean.exception.Preconditions.*;
import static java.lang.String.*;

import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.meoguri.linkocean.domain.bookmark.entity.Bookmark;
import com.meoguri.linkocean.domain.bookmark.entity.Favorite;
import com.meoguri.linkocean.domain.bookmark.persistence.FavoriteRepository;
import com.meoguri.linkocean.domain.bookmark.persistence.FindBookmarkByIdQuery;
import com.meoguri.linkocean.domain.profile.entity.Profile;
import com.meoguri.linkocean.domain.profile.persistence.FindProfileByIdQuery;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class FavoriteServiceImpl implements FavoriteService {

	private final FavoriteRepository favoriteRepository;

	private final FindProfileByIdQuery findProfileByIdQuery;
	private final FindBookmarkByIdQuery findBookmarkByIdQuery;

	@Override
	public void favorite(final long profileId, final long bookmarkId) {
		final Profile owner = findProfileByIdQuery.findById(profileId);
		final Bookmark bookmark = findBookmarkByIdQuery.findById(bookmarkId);

		final Optional<Favorite> oFavorite = favoriteRepository.findByProfileAndBookmark(owner, bookmark);
		checkUniqueConstraintIllegalCommand(oFavorite,
			format("illegal favorite command of profileId: %d on bookmarkId: %d", profileId, bookmarkId));

		favoriteRepository.save(new Favorite(owner, bookmark));
	}

	@Override
	public void unfavorite(final long profileId, final long bookmarkId) {
		final int count = favoriteRepository.deleteByProfile_idAndBookmark_id(profileId, bookmarkId);

		checkCondition(count == 1,
			"illegal unfavorite command of profileId " + profileId + " on " + "bookmarkId" + bookmarkId);
	}
}
