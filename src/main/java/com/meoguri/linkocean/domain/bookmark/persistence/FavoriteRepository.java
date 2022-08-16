package com.meoguri.linkocean.domain.bookmark.persistence;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.meoguri.linkocean.domain.bookmark.entity.Bookmark;
import com.meoguri.linkocean.domain.bookmark.entity.Favorite;
import com.meoguri.linkocean.domain.profile.entity.Profile;

public interface FavoriteRepository extends JpaRepository<Favorite, Long> {

	int deleteByProfile_idAndBookmark_id(long profileId, long bookmarkId);

	boolean existsByProfile_idAndBookmark(long profileId, Bookmark bookmark);

	Optional<Favorite> findByProfileAndBookmark(Profile profile, Bookmark bookmark);

	/* 즐겨찾기 중인 북마크의 id 집합 조회 */
	@Query("select f.bookmark.id "
		+ "from Favorite f "
		+ "where f.profile.id = :profileId "
		+ "and f.bookmark in :bookmarks")
	Set<Long> findBookmarkIdByProfileIdAndInBookmarks(long profileId, List<Bookmark> bookmarks);
}
