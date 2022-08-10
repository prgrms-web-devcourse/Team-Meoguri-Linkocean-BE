package com.meoguri.linkocean.domain.bookmark.persistence;

import java.util.List;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.meoguri.linkocean.domain.bookmark.entity.Bookmark;
import com.meoguri.linkocean.domain.bookmark.entity.Favorite;
import com.meoguri.linkocean.domain.profile.entity.Profile;

public interface FavoriteRepository extends JpaRepository<Favorite, Long> {

	int deleteByOwnerAndBookmark(Profile owner, Bookmark bookmark);

	boolean existsByOwner_idAndBookmark(long ownerId, Bookmark bookmark);

	/* 즐겨찾기 중인 북마크의 id 집합 조회 */
	@Query("select f.bookmark.id from Favorite f where f.owner.id = :ownerId and f.bookmark in :bookmarks")
	Set<Long> findBookmarkIdByOwnerIdAndBookmark(long ownerId, List<Bookmark> bookmarks);
}
