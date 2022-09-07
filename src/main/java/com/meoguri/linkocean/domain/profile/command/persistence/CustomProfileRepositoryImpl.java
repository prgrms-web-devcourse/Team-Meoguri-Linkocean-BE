package com.meoguri.linkocean.domain.profile.command.persistence;

import static com.meoguri.linkocean.domain.profile.entity.QProfile.*;

import javax.persistence.EntityManager;

import com.meoguri.linkocean.domain.profile.entity.Profile;
import com.meoguri.linkocean.support.querydsl.Querydsl4RepositorySupport;

public class CustomProfileRepositoryImpl extends Querydsl4RepositorySupport implements CustomProfileRepository {

	public CustomProfileRepositoryImpl(final EntityManager em) {
		super(Profile.class);
	}

	@Override
	public boolean existsByUsername(final String username) {
		return selectOne()
			.from(profile)
			.where(profile.username.eq(username))
			.fetchOne() != null;
	}

	@Override
	public boolean existsByUsernameExceptMe(final String updateUsername, final long profileId) {
		return selectOne()
			.from(profile)
			.where(
				profile.username.eq(updateUsername),
				profile.id.ne(profileId)
			).fetchOne() != null;
	}
}
