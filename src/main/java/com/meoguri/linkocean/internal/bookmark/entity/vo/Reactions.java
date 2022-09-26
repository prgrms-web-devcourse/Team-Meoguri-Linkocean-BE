package com.meoguri.linkocean.internal.bookmark.entity.vo;

import static java.util.stream.Collectors.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import javax.persistence.CollectionTable;
import javax.persistence.ElementCollection;
import javax.persistence.Embeddable;
import javax.persistence.JoinColumn;
import javax.persistence.UniqueConstraint;

@Embeddable
public class Reactions {

	@ElementCollection
	@CollectionTable(
		name = "reaction",
		joinColumns = @JoinColumn(name = "bookmark_id"),
		uniqueConstraints = @UniqueConstraint(columnNames = {"profile_id", "bookmark_id"})
	)
	private Set<Reaction> reactions = new HashSet<>();

	/* 리액션 요청 - 기존에 가지고 있던 리액션 타입 없었다면 null 반환 */
	public ReactionType requestReaction(final long profileId, final ReactionType requestType) {
		final Optional<Reaction> oReaction = reactions.stream().filter(r -> r.isOf(profileId)).findAny();
		ReactionType existedReactionType = oReaction.isEmpty() ? null : oReaction.get().getType();

		if (existedReactionType == null) {
			/* 북마크에 리액션을 가지고 있지 않으면 추가 */
			reactions.add(new Reaction(profileId, requestType));
		} else if (existedReactionType.equals(requestType)) {
			/* 북마크에 리액션을 가지고 있으며 같은 타입의 리액션을 하는 경우 취소 */
			reactions.remove(oReaction.get());
		} else {
			/* 북마크에 리액션을 가지고 있으며 다른 타입의 리액션을 하는 경우 변경 */
			oReaction.get().updateType(requestType);
		}

		return existedReactionType;
	}

	/* profile 의 bookmark 에 대한 리액션 여부 */
	public Map<ReactionType, Boolean> checkReaction(final long profileId) {
		final Optional<Reaction> oReaction = reactions.stream().filter(r -> r.isOf(profileId)).findAny();

		return Arrays.stream(ReactionType.values())
			.collect(toMap(
				reactionType -> reactionType,
				reactionType -> oReaction.map(reaction -> reaction.getType().equals(reactionType))
					.orElse(false)
			));
	}

	/* 리액션 카운트 맵 조회 */
	public Map<ReactionType, Long> countReactionGroup() {
		final Map<ReactionType, Long> countMap = new HashMap<>();
		Arrays.stream(ReactionType.values()).forEach(r -> countMap.put(r, 0L));

		reactions.stream().map(Reaction::getType).forEach(r -> countMap.put(r, countMap.get(r) + 1));
		return countMap;
	}
}
