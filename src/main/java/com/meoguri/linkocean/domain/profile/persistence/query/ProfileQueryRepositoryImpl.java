package com.meoguri.linkocean.domain.profile.persistence.query;

import javax.persistence.EntityManager;

import org.springframework.stereotype.Repository;

import com.meoguri.linkocean.domain.profile.entity.Profile;
import com.meoguri.linkocean.util.querydsl.Querydsl4RepositorySupport;

@Repository
public class ProfileQueryRepositoryImpl extends Querydsl4RepositorySupport implements ProfileQueryRepository {

	//TODO: profile 도메인 주입받는게 어색함... query에서 사용하는 모델을 주입하게 바꾸기
	public ProfileQueryRepositoryImpl(final EntityManager em) {
		super(Profile.class);
	}
}
