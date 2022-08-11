package com.meoguri.linkocean.domain.profile.persistence;

import static com.meoguri.linkocean.domain.profile.entity.QFollow.*;
import static com.meoguri.linkocean.domain.profile.entity.QProfile.*;
import static com.meoguri.linkocean.util.JoinInfoBuilder.Initializer.*;

import java.util.List;

import javax.persistence.EntityManager;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.meoguri.linkocean.domain.profile.entity.Profile;
import com.meoguri.linkocean.domain.profile.persistence.dto.ProfileFindCond;
import com.meoguri.linkocean.domain.profile.persistence.dto.UltimateProfileFindCond;
import com.meoguri.linkocean.util.Querydsl4RepositorySupport;
import com.querydsl.core.BooleanBuilder;

@Repository
public class CustomProfileRepositoryImpl extends Querydsl4RepositorySupport implements CustomProfileRepository {

	public CustomProfileRepositoryImpl(final EntityManager em) {
		super(Profile.class);
	}

	@Override
	public Page<Profile> ultimateFindProfiles(final UltimateProfileFindCond findCond, final Pageable pageable) {

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
		// final JPAQuery<Profile> base = selectFrom(profile);
		/*

		if (isFollower) {
			return applySlicing(
				pageable, base.where(followerOfUsername(isFollower, currentProfileId, username)));
		}

		if (isFollowee) {
			return applySlicing(
				pageable, base.where(followeeOfUsername(isFollowee, currentProfileId, username)));
		}

		return applySlicing(
			pageable, base.where(usernameContains(username)));*/
	}

	@Override
	public List<Profile> findFollowerProfilesBy(final ProfileFindCond findCond) {
		return selectFrom(profile)
			.where(
				followerOfUsername(findCond.getProfileId(), findCond.getUsername())
			)
			.offset(findCond.getOffset())
			.limit(findCond.getLimit())
			.fetch();
	}

	@Override
	public List<Profile> findFolloweeProfilesBy(final ProfileFindCond findCond) {
		return selectFrom(profile)
			.where(
				followeeOfUsername(findCond.getProfileId(), findCond.getUsername())
			)
			.offset(findCond.getOffset())
			.limit(findCond.getLimit())
			.fetch();
	}

	@Override
	public List<Profile> findByUsernameLike(final ProfileFindCond findCond) {
		return selectFrom(profile)
			.where(
				usernameLike(findCond.getUsername())
			)
			.offset(findCond.getOffset())
			.limit(findCond.getLimit())
			.fetch();
	}

	private BooleanBuilder followerOfUsername(
		boolean isFollower,
		Long profileId,
		String username
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
		boolean isFollowee,
		Long profileId,
		String username
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

	private BooleanBuilder followerOfUsername(long profileId, String username) {

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

	private BooleanBuilder followeeOfUsername(long profileId, String username) {

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

	/* profile.username like '%username%' escape '!' */
	private BooleanBuilder usernameLike(final String username) {

		return nullSafeBuilder(() -> profile.username.like(String.join(username, "%", "%")));
	}

	private BooleanBuilder usernameContains(final String username) {
		return nullSafeBuilder(() -> profile.username.containsIgnoreCase(username));
	}
}
