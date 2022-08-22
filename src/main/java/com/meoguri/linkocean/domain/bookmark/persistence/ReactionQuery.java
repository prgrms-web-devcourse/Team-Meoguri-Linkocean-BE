package com.meoguri.linkocean.domain.bookmark.persistence;

import static java.util.stream.Collectors.*;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;

import com.meoguri.linkocean.annotation.Query;
import com.meoguri.linkocean.domain.bookmark.entity.Bookmark;
import com.meoguri.linkocean.domain.bookmark.entity.Reaction;
import com.meoguri.linkocean.domain.bookmark.entity.vo.ReactionType;

import lombok.RequiredArgsConstructor;

@Query
@RequiredArgsConstructor
public class ReactionQuery {

	private final ReactionRepository reactionRepository;

	/* profile 의 bookmark 에 대한 리액션 여부 */
	public Map<ReactionType, Boolean> getReactionMap(final long profileId, final Bookmark bookmark) {
		final Optional<Reaction> oReaction = reactionRepository.findByProfile_idAndBookmark(profileId, bookmark);

		return Arrays.stream(ReactionType.values())
			.collect(toMap(
				reactionType -> reactionType,
				reactionType -> oReaction.map(reaction -> reaction.getType().equals(reactionType))
					.orElse(false)
			));
	}
}
