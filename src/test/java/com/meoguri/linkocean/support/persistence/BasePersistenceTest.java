package com.meoguri.linkocean.support.persistence;

import static com.meoguri.linkocean.support.common.Fixture.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.transaction.annotation.Transactional;

import com.meoguri.linkocean.domain.user.entity.vo.OAuthType;
import com.meoguri.linkocean.domain.user.persistence.UserRepository;

@Transactional
@DataJpaTest
public class BasePersistenceTest {

	@Autowired
	private UserRepository userRepository;

	protected long 사용자_저장(final String email, final OAuthType oAuthType) {
		return userRepository.save(createUser(email, oAuthType)).getId();
	}
}
