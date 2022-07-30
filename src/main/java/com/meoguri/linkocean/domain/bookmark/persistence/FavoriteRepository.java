package com.meoguri.linkocean.domain.bookmark.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import com.meoguri.linkocean.domain.bookmark.entity.Bookmark;
import com.meoguri.linkocean.domain.bookmark.entity.Favorite;
import com.meoguri.linkocean.domain.profile.entity.Profile;

public interface FavoriteRepository extends JpaRepository<Favorite, Long> {

	int deleteByOwnerAndBookmark(Profile owner, Bookmark bookmark);
}
