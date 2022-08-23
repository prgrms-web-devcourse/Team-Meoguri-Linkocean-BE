package com.meoguri.linkocean.domain.profile.persistence.command;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.meoguri.linkocean.annotation.Query;
import com.meoguri.linkocean.domain.profile.entity.Profile;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Query
public class CheckIsFollowQuery {

	private final FollowRepository followRepository;

	public boolean isFollow(final long followerId, final Profile followee) {
		return followRepository.existsByFollower_idAndFollowee(followerId, followee);
	}

	/* profileId 사용자가 targets 프로필 목록을 팔로우 즐겨찾기를 했는지 입력받은 순서대로 말아준다 */
	public List<Boolean> isFollows(final long profileId, final List<Profile> targets) {
		final Set<Long> followerProfileIds = followRepository.findFolloweeIdsFollowedBy(profileId, targets);

		return targets.stream()
			.map(Profile::getId)
			.map(followerProfileIds::contains)
			.collect(Collectors.toList());
	}
}
