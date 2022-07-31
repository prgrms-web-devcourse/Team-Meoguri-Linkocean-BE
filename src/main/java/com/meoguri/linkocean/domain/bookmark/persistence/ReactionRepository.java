package com.meoguri.linkocean.domain.bookmark.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.meoguri.linkocean.domain.bookmark.entity.Bookmark;
import com.meoguri.linkocean.domain.bookmark.entity.Reaction;
import com.meoguri.linkocean.domain.bookmark.entity.Reaction.ReactionType;
import com.meoguri.linkocean.domain.profile.entity.Profile;

public interface ReactionRepository extends JpaRepository<Reaction, Long> {

	int deleteByProfileAndBookmarkAndType(Profile profile, Bookmark bookmark, ReactionType type);

	@Query("select count(r) from Reaction r where r.bookmark = :bookmark and r.type = 'LIKE'")
	long countLikeByBookmark(Bookmark bookmark);

	@Query("select count(r) from Reaction r where r.bookmark = :bookmark and r.type = 'HATE'")
	long countHateByBookmark(Bookmark bookmark);
}
