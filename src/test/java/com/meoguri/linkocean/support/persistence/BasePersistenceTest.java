package com.meoguri.linkocean.support.persistence;

import static com.meoguri.linkocean.support.common.Fixture.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.transaction.annotation.Transactional;

import com.meoguri.linkocean.domain.bookmark.entity.vo.Category;
import com.meoguri.linkocean.domain.profile.entity.Profile;
import com.meoguri.linkocean.domain.profile.persistence.ProfileRepository;
import com.meoguri.linkocean.domain.user.entity.User;
import com.meoguri.linkocean.domain.user.entity.vo.OAuthType;
import com.meoguri.linkocean.domain.user.persistence.UserRepository;

@Transactional
@DataJpaTest
public class BasePersistenceTest {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private ProfileRepository profileRepository;

	protected User 사용자_저장(final String email, final OAuthType oAuthType) {
		return userRepository.save(createUser(email, oAuthType));
	}

	protected Profile 프로필_저장(final String username, final Category... categories) {
		return profileRepository.save(createProfile(username, categories));
	}

	protected void 프로필_등록(final User user, final Profile profile) {
		user.registerProfile(profile);
	}

	protected Profile 프로필_저장_등록(final User user, final String username, final Category... categories) {
		final Profile profile = profileRepository.save(createProfile(username, categories));
		user.registerProfile(profile);
		return profile;
	}

}
