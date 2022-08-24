package com.meoguri.linkocean.domain.profile.persistence.command;

import static com.meoguri.linkocean.domain.bookmark.entity.vo.Category.*;
import static com.meoguri.linkocean.domain.user.entity.vo.OAuthType.*;
import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;

import com.meoguri.linkocean.domain.profile.entity.Profile;
import com.meoguri.linkocean.test.support.persistence.BasePersistenceTest;

@Import(FindProfileByIdQuery.class)
class FindProfileByIdQueryTest extends BasePersistenceTest {

	@Autowired
	private FindProfileByIdQuery query;

	@Test
	void 프로필_아이디로_조회_성공() {
		//given
		final Profile profile = 사용자_프로필_동시_저장("haha@gmail.com", GOOGLE, "haha", IT, ART);
		final long profileId = profile.getId();

		//when
		final Profile foundProfile = query.findById(profileId);

		//then
		assertThat(foundProfile).isEqualTo(profile);
	}
}
