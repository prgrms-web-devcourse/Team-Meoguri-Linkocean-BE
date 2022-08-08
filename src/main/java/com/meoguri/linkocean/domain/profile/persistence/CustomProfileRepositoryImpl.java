package com.meoguri.linkocean.domain.profile.persistence;

import static com.meoguri.linkocean.domain.profile.entity.QFollow.*;
import static com.meoguri.linkocean.domain.profile.entity.QProfile.*;
import static com.meoguri.linkocean.util.JoinInfoBuilder.Initializer.*;

import java.util.List;

import javax.persistence.EntityManager;

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
				usernameLike(username)
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
				usernameLike(username)
			))
		);
	}

	/* profile.username like '%username%' escape '!' */
	private BooleanBuilder usernameLike(final String username) {

		return nullSafeBuilder(() -> profile.username.like(String.join(username, "%", "%")));
	}
}
