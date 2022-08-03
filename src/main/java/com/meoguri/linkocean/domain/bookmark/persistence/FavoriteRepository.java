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

	boolean existsByOwnerAndBookmark(Profile owner, Bookmark bookmark);

	/**
	 * bookmarks에 대한 owner의 즐겨찾기 PK 집합을 가져오는데 사용한다.
	 */
	@Query("select f.bookmark.id from Favorite f where f.owner = :owner and f.bookmark in :bookmarks")
	Set<Long> findAllFavoriteByProfileAndBookmarks(Profile owner, List<Bookmark> bookmarks);
}
