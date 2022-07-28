package com.meoguri.linkocean.domain.profile.persistence;

import static com.meoguri.linkocean.domain.profile.entity.QFollow.*;
import static com.meoguri.linkocean.domain.profile.entity.QProfile.*;
import static com.meoguri.linkocean.util.QueryDslUtil.*;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.meoguri.linkocean.domain.profile.entity.Profile;
import com.meoguri.linkocean.domain.profile.persistence.dto.FindProfileCond;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.JPQLQueryFactory;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Repository
public class CustomProfileRepositoryImpl implements CustomProfileRepository {

	private final JPQLQueryFactory query;

	@Override
	public List<Profile> findFollowerProfilesBy(final FindProfileCond findCond) {

		return query
			.selectFrom(profile)
			.where(
				followerOfUsername(findCond.getProfileId(), findCond.getUsername())
				// targetNameEq(follow.follower, findCond.getUsername())
			)
			.offset(findCond.getOffset())
			.limit(findCond.getLimit())
			.fetch();
	}

	@Override
	public List<Profile> findFolloweeProfilesBy(final FindProfileCond findCond) {
		return query
			.selectFrom(profile)
			.where(
				followeeOfUsername(findCond.getProfileId(), findCond.getUsername())
				// followeeNameEq(findCond.getUsername())
			)
			.offset(findCond.getOffset())
			.limit(findCond.getLimit())
			.fetch();
	}

	private BooleanBuilder followerOfUsername(long profileId, String username) {

		return nullSafeBuilder(() -> profile.id.in(

			joinIf(
				username != null,
				query
					.select(follow.follower.id)
					.from(follow),
				join(follow.follower, profile),
				on(follow.follower.id.eq(profile.id))
			).where(
				follow.followee.id.eq(profileId),
				nullSafeBuilder(() -> profile.username.eq(username))
			)
		));
	}

	private BooleanBuilder followeeOfUsername(long profileId, String username) {

		return nullSafeBuilder(() -> profile.id.in(

			joinIf(
				username != null,
				query
					.select(follow.followee.id)
					.from(follow),
				join(follow.followee, profile),
				on(follow.followee.id.eq(profile.id))
			).where(
				follow.follower.id.eq(profileId),
				nullSafeBuilder(() -> profile.username.eq(username))
			)
		));
	}
}
