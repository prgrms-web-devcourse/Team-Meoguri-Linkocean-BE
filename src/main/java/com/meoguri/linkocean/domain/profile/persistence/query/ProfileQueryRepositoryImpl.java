package com.meoguri.linkocean.domain.profile.persistence.query;

import static com.meoguri.linkocean.domain.profile.entity.QFollow.*;
import static com.meoguri.linkocean.domain.profile.entity.QProfile.*;
import static com.meoguri.linkocean.util.querydsl.JoinInfoBuilder.Initializer.*;

import javax.persistence.EntityManager;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Repository;

import com.meoguri.linkocean.domain.profile.entity.Profile;
import com.meoguri.linkocean.domain.profile.persistence.command.dto.ProfileFindCond;
import com.meoguri.linkocean.util.querydsl.Querydsl4RepositorySupport;
import com.querydsl.core.BooleanBuilder;

@Repository
public class ProfileQueryRepositoryImpl extends Querydsl4RepositorySupport implements ProfileQueryRepository {

	//TODO: profile 도메인 주입받는게 어색함... query에서 사용하는 모델을 주입하게 바꾸기
	public ProfileQueryRepositoryImpl(final EntityManager em) {
		super(Profile.class);
	}

	@Override
	public Slice<Profile> findProfiles(final ProfileFindCond findCond, final Pageable pageable) {

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
