package com.meoguri.linkocean.domain.profile.persistence;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.meoguri.linkocean.annotation.Query;
import com.meoguri.linkocean.domain.bookmark.entity.Bookmark;
import com.meoguri.linkocean.domain.bookmark.persistence.FavoriteRepository;
import com.meoguri.linkocean.domain.profile.entity.Profile;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Query
public class CheckIsFavoriteQuery {

	private final FavoriteRepository favoriteRepository;

	public boolean isFavorite(final Profile owner, final Bookmark bookmark) {
		return favoriteRepository.existsByOwnerAndBookmark(owner, bookmark);
	}

	/* owner 가 북마크 목록에 대해 즐겨찾기를 했는지 입력받은 순서대로 말아준다 */
	public List<Boolean> isFavorites(final long ownerId, final List<Bookmark> bookmarks) {

		final Set<Long> favoriteBookmarkIds = favoriteRepository.findByOwnerIdAndBookmark(ownerId, bookmarks);

		return bookmarks.stream()
			.map(Bookmark::getId)
			.map(favoriteBookmarkIds::contains)
			.collect(Collectors.toList());
	}
}
