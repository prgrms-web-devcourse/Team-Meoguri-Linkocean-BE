package com.meoguri.linkocean.domain.profile.service;

import static com.meoguri.linkocean.common.Assertions.*;
import static java.util.Collections.*;
import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import com.meoguri.linkocean.domain.profile.service.dto.FollowCommand;
import com.meoguri.linkocean.domain.profile.service.dto.RegisterProfileCommand;
import com.meoguri.linkocean.domain.user.entity.User;
import com.meoguri.linkocean.domain.user.repository.UserRepository;

@Transactional
@SpringBootTest
class FollowServiceImplTest {

	@Autowired
	private FollowService followService;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private ProfileService profileService;

	private long user1Id;
	private long user2Id;

	private long user1ProfileId;
	private long user2ProfileId;

	@BeforeEach
	void setUp() {
		user1Id = userRepository.save(new User("haha@gmail.com", "GOOGLE")).getId();
		user2Id = userRepository.save(new User("papa@gmail.com", "GOOGLE")).getId();

		final RegisterProfileCommand command1 = new RegisterProfileCommand(user1Id, "haha", emptyList());
		final RegisterProfileCommand command2 = new RegisterProfileCommand(user2Id, "papa", emptyList());

		user1ProfileId = profileService.registerProfile(command1);
		user2ProfileId = profileService.registerProfile(command2);
	}

	@Test
	void 팔로우_성공() {
		//given
		final FollowCommand command = new FollowCommand(user1ProfileId, user2ProfileId);

		//when
		followService.follow(command);

		//then
		assertThat(profileService.getByProfileId(user1ProfileId, user1ProfileId).getFolloweeCount()).isEqualTo(1);
		assertThat(profileService.getByProfileId(user2ProfileId, user2ProfileId).getFollowerCount()).isEqualTo(1);
	}

	@Test
	void 팔로우_두번_요청_실패() {
		//given
		final FollowCommand command = new FollowCommand(user1ProfileId, user2ProfileId);
		followService.follow(command);

		//when then
		assertThatDataIntegrityViolationException()
			.isThrownBy(() -> followService.follow(command));
	}

	@Test
	void 언팔로우_성공() {
		//given
		final FollowCommand command = new FollowCommand(user1ProfileId, user2ProfileId);
		followService.follow(command);

		//when
		followService.unfollow(command);

		//then
		assertThat(profileService.getByProfileId(user1ProfileId, user1ProfileId).getFolloweeCount()).isZero();
		assertThat(profileService.getByProfileId(user2ProfileId, user2ProfileId).getFollowerCount()).isZero();
	}

	@Test
	void 언팔로우_실패() {
		//given
		final FollowCommand command = new FollowCommand(user1ProfileId, user2ProfileId);

		//when then
		assertThatLinkoceanRuntimeException()
			.isThrownBy(() -> followService.unfollow(command));
	}
}
