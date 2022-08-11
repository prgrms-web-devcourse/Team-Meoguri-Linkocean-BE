package com.meoguri.linkocean.domain.profile.persistence;

import static com.meoguri.linkocean.domain.profile.entity.QFollow.*;
import static com.meoguri.linkocean.domain.profile.entity.QProfile.*;
import static com.meoguri.linkocean.util.JoinInfoBuilder.Initializer.*;

import javax.persistence.EntityManager;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.meoguri.linkocean.domain.profile.entity.Profile;
import com.meoguri.linkocean.domain.profile.persistence.dto.ProfileFindCond;
import com.meoguri.linkocean.util.Querydsl4RepositorySupport;
import com.querydsl.core.BooleanBuilder;

@Repository
public class CustomProfileRepositoryImpl extends Querydsl4RepositorySupport implements CustomProfileRepository {

	public CustomProfileRepositoryImpl(final EntityManager em) {
		super(Profile.class);
	}

	@Override
	public Page<Profile> findProfiles(final ProfileFindCond findCond, final Pageable pageable) {

		final Long currentProfileId = findCond.getProfileId();
		final boolean isFollower = findCond.isFollower();
		final boolean isFollowee = findCond.isFollowee();
		final String username = findCond.getUsername();

		return applySlicing(
			pageable,
			selectFrom(profile)
				.where(
					followerOfUsername(isFollower, currentProfileId, username),
					followeeOfUsername(isFollowee, currentProfileId, username),
					usernameContains(username)
				)
		);
	}

	private BooleanBuilder followerOfUsername(
		final boolean isFollower,
		final Long profileId,
		final String username
	) {
		if (!isFollower) {
			return new BooleanBuilder();
		}

		return nullSafeBuilder(() -> profile.in(
			joinIf(
				username != null,
				select(follow.follower)
					.from(follow),
				() -> join(follow.follower, profile)
					.on(follow.follower.id.eq(profile.id))
			).where(
				follow.followee.id.eq(profileId),
				usernameContains(username)
			))
		);
	}

	private BooleanBuilder followeeOfUsername(
		final boolean isFollowee,
		final Long profileId,
		final String username
	) {
		if (!isFollowee) {
			return new BooleanBuilder();
		}

		return nullSafeBuilder(() -> profile.in(
			joinIf(
				username != null,
				select(follow.followee)
					.from(follow),
				() -> join(follow.followee, profile)
					.on(follow.followee.id.eq(profile.id))
			).where(
				follow.follower.id.eq(profileId),
				usernameContains(username)
			))
		);
	}

	private BooleanBuilder usernameContains(final String username) {
		return nullSafeBuilder(() -> profile.username.containsIgnoreCase(username));
	}
}
