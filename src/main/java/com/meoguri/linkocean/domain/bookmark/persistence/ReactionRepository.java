package com.meoguri.linkocean.domain.bookmark.persistence;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.persistence.Tuple;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.meoguri.linkocean.domain.bookmark.entity.Bookmark;
import com.meoguri.linkocean.domain.bookmark.entity.Reaction;
import com.meoguri.linkocean.domain.bookmark.entity.vo.ReactionType;

public interface ReactionRepository extends JpaRepository<Reaction, Long> {

	@Modifying(clearAutomatically = true)
	@Query("delete "
		+ "from Reaction r "
		+ "where r.profile.id = :profileId "
		+ "and r.bookmark.id = :bookmarkId")
	void deleteByProfile_idAndBookmark_id(long profileId, long bookmarkId);

	Optional<Reaction> findByProfile_idAndBookmark(long profileId, Bookmark bookmark);

	@Query("select r.type as type, count(r) as cnt "
		+ "from Reaction r "
		+ "where r.bookmark = :bookmark "
		+ "group by r.type")
	List<Tuple> countReactionGroupInternal(Bookmark bookmark);

	/* 북마크의 리액션 별 카운트 조회 */
	default Map<ReactionType, Long> countReactionGroup(Bookmark bookmark) {
		return countReactionGroupInternal(bookmark)
			.stream()
			.collect(Collectors.toMap(
				tuple -> ((ReactionType)tuple.get("type")),
				tuple -> ((long)tuple.get("cnt")))
			);
	}

	@Modifying(clearAutomatically = true)
	@Query("update Reaction r "
		+ "set r.type = :type "
		+ "where r.profile.id = :profileId "
		+ "and r.bookmark.id = :bookmarkId")
	void updateReaction(long profileId, long bookmarkId, ReactionType type);
}
