package com.meoguri.linkocean.domain.profile.persistence;

import static com.meoguri.linkocean.domain.profile.entity.QFollow.*;
import static com.meoguri.linkocean.domain.profile.entity.QProfile.*;
import static com.meoguri.linkocean.util.QueryDslUtil.*;

import java.util.List;

import javax.persistence.EntityManager;

import org.springframework.stereotype.Repository;

import com.meoguri.linkocean.domain.profile.entity.Profile;
import com.meoguri.linkocean.domain.profile.persistence.dto.ProfileFindCond;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.JPQLQueryFactory;
import com.querydsl.jpa.impl.JPAQueryFactory;

@Repository
public class CustomProfileRepositoryImpl implements CustomProfileRepository {

	private final JPQLQueryFactory query;

	public CustomProfileRepositoryImpl(final EntityManager em) {
		this.query = new JPAQueryFactory(em);
	}

	@Override
	public List<Profile> findFollowerProfilesBy(final ProfileFindCond findCond) {

		return query
			.selectFrom(profile)
			.where(
				followerOfUsername(findCond.getProfileId(), findCond.getUsername())
			)
			.offset(findCond.getOffset())
			.limit(findCond.getLimit())
			.fetch();
	}

	@Override
	public List<Profile> findFolloweeProfilesBy(final ProfileFindCond findCond) {
		return query
			.selectFrom(profile)
			.where(
				followeeOfUsername(findCond.getProfileId(), findCond.getUsername())
			)
			.offset(findCond.getOffset())
			.limit(findCond.getLimit())
			.fetch();
	}

	@Override
	public List<Profile> findByUsernameLike(final ProfileFindCond findCond) {
		return query
			.selectFrom(profile)
			.where(
				usernameLike(findCond.getUsername())
			)
			.offset(findCond.getOffset())
			.limit(findCond.getLimit())
			.fetch();
	}

	private BooleanBuilder followerOfUsername(long profileId, String username) {

		return nullSafeBuilder(() -> profile.in(
			joinIf(query
					.select(follow.follower)
					.from(follow),
				join(follow.follower, profile),
				on(follow.follower.id.eq(profile.id)),
				when(username != null)
			).where(
				follow.followee.id.eq(profileId),
				usernameLike(username)
			))
		);
	}

	private BooleanBuilder followeeOfUsername(long profileId, String username) {

		return nullSafeBuilder(() -> profile.in(
			joinIf(query
					.select(follow.followee)
					.from(follow),
				join(follow.followee, profile),
				on(follow.followee.id.eq(profile.id)),
				when(username != null)
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
