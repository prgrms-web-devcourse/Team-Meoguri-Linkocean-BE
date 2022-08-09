package com.meoguri.linkocean.domain.bookmark.persistence;

import static com.meoguri.linkocean.domain.bookmark.entity.Reaction.ReactionType.*;
import static java.util.stream.Collectors.*;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;

import com.meoguri.linkocean.annotation.Query;
import com.meoguri.linkocean.domain.bookmark.entity.Bookmark;
import com.meoguri.linkocean.domain.bookmark.entity.Reaction;
import com.meoguri.linkocean.domain.bookmark.entity.Reaction.ReactionType;
import com.meoguri.linkocean.domain.profile.entity.Profile;

import lombok.RequiredArgsConstructor;

@Query
@RequiredArgsConstructor
public class ReactionQuery {

	private final ReactionRepository reactionRepository;

	/**
	 * @param bookmark
	 * @return 북마크의 리액션별 카운트
	 */
	public Map<ReactionType, Long> getReactionCountMap(Bookmark bookmark) {
		final Map<ReactionType, Long> reactionCountMap = reactionRepository.countReactionGroup(bookmark);

		Arrays.stream(ReactionType.values())
			.filter(reactionType -> !reactionCountMap.containsKey(reactionType))
			.forEach(reactionType -> reactionCountMap.put(reactionType, 0L));

		return reactionCountMap;
	}

	/**
	 * @param profile
	 * @param bookmark
	 * @return profile 의 bookmark 에 대한 리액션 여부
	 */
	public Map<ReactionType, Boolean> getReactionMap(final Profile profile, final Bookmark bookmark) {
		final Optional<Reaction> oReaction = reactionRepository.findByProfileAndBookmark(profile, bookmark);

		return Arrays.stream(ReactionType.values())
			.collect(toMap(
				reactionType -> reactionType,
				reactionType -> oReaction.map(reaction -> of(reaction.getType()).equals(reactionType)).orElse(false)
			));
	}
}
