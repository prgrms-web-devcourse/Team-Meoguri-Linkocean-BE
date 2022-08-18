package com.meoguri.linkocean.domain.user.persistence;

import static com.meoguri.linkocean.domain.util.Fixture.*;
import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import com.meoguri.linkocean.domain.user.entity.User;

@Import(FindUserByIdQuery.class)
@DataJpaTest
class FindUserByIdQueryTest {

	@Autowired
	private FindUserByIdQuery query;

	@Autowired
	private UserRepository userRepository;

	@Test
	void 아이디로_사용자_조회_성공() {
		//given
		final User user = createUser();
		userRepository.save(user);

		//when
		final User foundUser = query.findById(user.getId());

		//then
		assertThat(foundUser).isEqualTo(user);
	}
}
