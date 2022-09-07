package com.meoguri.linkocean.domain.profile.query.persistence;

import static com.meoguri.linkocean.domain.profile.entity.QFollow.*;
import static com.meoguri.linkocean.domain.profile.entity.QProfile.*;
import static com.meoguri.linkocean.util.querydsl.JoinInfoBuilder.Initializer.*;

import javax.persistence.EntityManager;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Repository;

import com.meoguri.linkocean.domain.profile.entity.Profile;
import com.meoguri.linkocean.domain.profile.query.persistence.dto.ProfileFindCond;
import com.meoguri.linkocean.util.querydsl.Querydsl4RepositorySupport;
import com.querydsl.core.BooleanBuilder;

@Repository
public class CustomProfileQueryRepositoryImpl extends Querydsl4RepositorySupport
	implements CustomProfileQueryRepository {

	public CustomProfileQueryRepositoryImpl(final EntityManager em) {
		super(Profile.class);
	}

	@Override
	public Slice<Profile> findProfiles(final ProfileFindCond findCond, final Pageable pageable) {
		final Long currentProfileId = findCond.getProfileId();
		final String username = findCond.getUsername();

		final boolean isFollower = findCond.isFollower();
		final boolean isFollowee = findCond.isFollowee();

		return applyDynamicSlicing(
			pageable,
			selectFrom(profile),
			where(
				always(usernameContains(username)),
				whereIf(isFollower, () -> followerOfUsername(currentProfileId, username)),
				whereIf(isFollowee, () -> followeeOfUsername(currentProfileId, username))
			)
		);
	}

	private BooleanBuilder followerOfUsername(final Long profileId, final String username) {
		return nullSafeBuilder(() -> profile.in(applyDynamicJoin(
			select(follow.id.follower).from(follow),
			joinIf(username != null,
				() -> join(follow.id.follower, profile)
					.on(follow.id.follower.id.eq(profile.id))))
			.where(
				follow.id.followee.id.eq(profileId),
				usernameContains(username)
			)
		));
	}

	private BooleanBuilder followeeOfUsername(final Long profileId, final String username) {
		return nullSafeBuilder(() -> profile.in(applyDynamicJoin(
			select(follow.id.followee).from(follow),
			joinIf(username != null,
				() -> join(follow.id.followee, profile)
					.on(follow.id.followee.id.eq(profile.id))))
			.where(
				follow.id.follower.id.eq(profileId),
				usernameContains(username)
			)
		));
	}

	private BooleanBuilder usernameContains(final String username) {
		return nullSafeBuilder(() -> profile.username.containsIgnoreCase(username));
	}
}
