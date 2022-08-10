package com.meoguri.linkocean.domain.bookmark.persistence;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.persistence.Tuple;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.meoguri.linkocean.domain.bookmark.entity.Bookmark;
import com.meoguri.linkocean.domain.bookmark.entity.Reaction;
import com.meoguri.linkocean.domain.bookmark.entity.Reaction.ReactionType;
import com.meoguri.linkocean.domain.profile.entity.Profile;

public interface ReactionRepository extends JpaRepository<Reaction, Long> {

	void deleteByProfileAndBookmarkAndType(Profile profile, Bookmark bookmark, ReactionType type);

	/* TODO - 리팩터링 제거 대상입니당, 항상 LIKE 로만 호출 되는데 ReactionType 을 받는 점이 아쉽네용 */
	long countReactionByBookmarkAndType(Bookmark bookmark, ReactionType reactionType);

	Optional<Reaction> findByProfile_idAndBookmark(long profileId, Bookmark bookmark);

	@Query("select r.type as type, count(r) as cnt "
		+ "from Reaction r "
		+ "where r.bookmark = ?1 "
		+ "group by r.type"
	)
	List<Tuple> countReactionGroupInternal(Bookmark bookmark);

	default Map<ReactionType, Long> countReactionGroup(Bookmark bookmark) {
		return countReactionGroupInternal(bookmark)
			.stream()
			.collect(Collectors.toMap(
				tuple -> ((ReactionType)tuple.get("type")),
				tuple -> ((long)tuple.get("cnt")))
			);
	}
}
