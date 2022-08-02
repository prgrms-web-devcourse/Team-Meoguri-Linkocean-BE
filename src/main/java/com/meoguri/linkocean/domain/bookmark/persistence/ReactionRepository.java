package com.meoguri.linkocean.domain.bookmark.persistence;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.meoguri.linkocean.domain.bookmark.entity.Bookmark;
import com.meoguri.linkocean.domain.bookmark.entity.Reaction;
import com.meoguri.linkocean.domain.bookmark.entity.Reaction.ReactionType;
import com.meoguri.linkocean.domain.profile.entity.Profile;

public interface ReactionRepository extends JpaRepository<Reaction, Long> {

	int deleteByProfileAndBookmarkAndType(Profile profile, Bookmark bookmark, ReactionType type);

	long countReactionByBookmarkAndType(Bookmark bookmark, ReactionType reactionType);

	Optional<Reaction> findByProfileAndBookmark(Profile profile, Bookmark bookmark);
}
