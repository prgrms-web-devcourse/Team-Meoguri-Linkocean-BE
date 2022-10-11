package com.meoguri.linkocean.internal.profile.entity;

import static com.meoguri.linkocean.exception.Preconditions.*;
import static java.util.stream.Collectors.*;
import static lombok.AccessLevel.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Embeddable;
import javax.persistence.JoinColumn;

import com.meoguri.linkocean.internal.bookmark.entity.Bookmark;

import lombok.NoArgsConstructor;

/**
 * 사용자가 즐겨찾기한 북마크 아이디 목록
 */
@Embeddable
@NoArgsConstructor(access = PROTECTED)
public class FavoriteBookmarkIds {

	@ElementCollection
	@CollectionTable(
		name = "favorite",
		joinColumns = @JoinColumn(name = "owner_id")
	)
	@Column(name = "bookmark_id")
	private Set<Long> favoriteBookmarkIds = new HashSet<>();

	/* 즐겨찾기 추가 */
	void favorite(final Bookmark bookmark) {
		checkCondition(!favoriteBookmarkIds.contains(bookmark.getId()), "illegal favorite command");
		favoriteBookmarkIds.add(bookmark.getId());
	}

	/* 즐겨찾기 취소 */
	void unfavorite(final Bookmark bookmark) {
		checkCondition(favoriteBookmarkIds.contains(bookmark.getId()), "illegal unfavorite command");
		favoriteBookmarkIds.remove(bookmark.getId());
	}

	/* 북마크 즐겨찾기 여부 확인 */
	boolean isFavoriteBookmark(final Bookmark bookmark) {
		return favoriteBookmarkIds.contains(bookmark.getId());
	}

	/* 북마크 목록 즐겨찾기 여부 확인 */
	List<Boolean> isFavoriteBookmarks(final List<Bookmark> bookmarks) {
		return bookmarks.stream()
			.map(Bookmark::getId)
			.map(favoriteBookmarkIds::contains)
			.collect(toList());
	}
}
