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
	 * bookmarks 에 대한 owner 의 즐겨찾기 PK 집합을 가져오는데 사용한다.
	 */
	@Query("select f.bookmark.id from Favorite f where f.owner.id = :ownerId and f.bookmark in :bookmarks")
	Set<Long> findByOwnerIdAndBookmark(long ownerId, List<Bookmark> bookmarks);
}
