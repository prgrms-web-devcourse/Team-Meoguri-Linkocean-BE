package com.meoguri.linkocean.internal.profile.query.persistence;

import static com.meoguri.linkocean.internal.profile.entity.QFollow.*;
import static com.meoguri.linkocean.internal.profile.entity.QProfile.*;

import javax.persistence.EntityManager;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Repository;

import com.meoguri.linkocean.internal.profile.entity.Profile;
import com.meoguri.linkocean.internal.profile.query.persistence.dto.ProfileFindCond;
import com.meoguri.linkocean.support.internal.persistence.querydsl.Querydsl4RepositorySupport;
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

	private BooleanBuilder followerOfUsername(final Long currentProfileId, final String username) {
		return nullSafeBuilder(() -> profile.id.in(
			select(follow.id.followerId)
				.from(follow)
				.where(
					usernameContains(username),
					follow.id.followeeId.eq(currentProfileId)
				)
		));
	}

	private BooleanBuilder followeeOfUsername(final Long currentProfileId, final String username) {
		return nullSafeBuilder(() -> profile.id.in(
			select(follow.id.followeeId)
				.from(follow)
				.where(
					usernameContains(username),
					follow.id.followerId.eq(currentProfileId)
				)
		));
	}

	private BooleanBuilder usernameContains(final String username) {
		return nullSafeBuilder(() -> profile.username.containsIgnoreCase(username));
	}
}
