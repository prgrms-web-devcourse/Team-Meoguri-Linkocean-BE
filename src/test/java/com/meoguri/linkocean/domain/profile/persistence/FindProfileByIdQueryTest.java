package com.meoguri.linkocean.domain.profile.persistence;

import static com.meoguri.linkocean.domain.util.Fixture.*;
import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import com.meoguri.linkocean.domain.profile.entity.Profile;
import com.meoguri.linkocean.domain.user.repository.UserRepository;

@Import(FindProfileByIdQuery.class)
@DataJpaTest
class FindProfileByIdQueryTest {

	private Profile profile;

	@Autowired
	private FindProfileByIdQuery query;

	@Autowired
	private ProfileRepository profileRepository;

	@Autowired
	private UserRepository userRepository;

	@BeforeEach
	void setUp() {
		profile = createProfile(userRepository.save(createUser()));
	}

	@Test
	void 프로필_아이디로_조회_성공() {
		//given
		profileRepository.save(profile);

		//when
		final Profile foundProfile = query.findById(profile.getId());

		//then
		assertThat(foundProfile).isEqualTo(profile);
	}
}