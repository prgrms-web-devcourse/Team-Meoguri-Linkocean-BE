package com.meoguri.linkocean.domain.user.persistence;

import static com.meoguri.linkocean.domain.user.entity.vo.OAuthType.*;
import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;

import com.meoguri.linkocean.domain.user.entity.User;
import com.meoguri.linkocean.test.support.persistence.BasePersistenceTest;

@Import(FindUserByIdQuery.class)
class FindUserByIdQueryTest extends BasePersistenceTest {

	@Autowired
	private FindUserByIdQuery query;

	@Test
	void 아이디로_사용자_조회_성공() {
		//given
		User savedUser = 사용자_저장("haha@gmail.com", GOOGLE);

		//when
		final User foundUser = query.findById(savedUser.getId());

		//then
		assertThat(foundUser).isEqualTo(savedUser);
	}
}
