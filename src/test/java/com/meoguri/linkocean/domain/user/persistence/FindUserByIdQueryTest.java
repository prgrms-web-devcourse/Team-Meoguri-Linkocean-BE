package com.meoguri.linkocean.domain.user.persistence;

import static com.meoguri.linkocean.domain.user.entity.vo.OAuthType.*;
import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;

import com.meoguri.linkocean.domain.user.entity.User;
import com.meoguri.linkocean.support.persistence.BasePersistenceTest;

@Import(FindUserByIdQuery.class)
class FindUserByIdQueryTest extends BasePersistenceTest {

	@Autowired
	private FindUserByIdQuery query;

	@Test
	void 아이디로_사용자_조회_성공() {
		//given
		long userId = 사용자_저장("haha@gmail.com", GOOGLE);

		//when
		final User foundUser = query.findById(userId);

		//then
		assertThat(foundUser.getId()).isEqualTo(userId);
	}
}
