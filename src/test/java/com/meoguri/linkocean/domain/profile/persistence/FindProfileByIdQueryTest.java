package com.meoguri.linkocean.domain.profile.persistence;

import static com.meoguri.linkocean.domain.bookmark.entity.vo.Category.*;
import static com.meoguri.linkocean.domain.user.entity.vo.OAuthType.*;
import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;

import com.meoguri.linkocean.domain.profile.entity.Profile;
import com.meoguri.linkocean.domain.user.entity.User;
import com.meoguri.linkocean.support.persistence.BasePersistenceTest;

@Import(FindProfileByIdQuery.class)
class FindProfileByIdQueryTest extends BasePersistenceTest {

	@Autowired
	private FindProfileByIdQuery query;

	@Test
	void 프로필_아이디로_조회_성공() {
		//given
		final User user = 사용자_저장("haha@gmail.com", GOOGLE);
		final Profile profile = 프로필_저장_등록(user, "haha", IT, ART);

		//when
		final Profile foundProfile = query.findById(profile.getId());

		//then
		assertThat(foundProfile).isEqualTo(profile);
	}
}
