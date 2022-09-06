package com.meoguri.linkocean.domain.profile.command.service;

import static com.meoguri.linkocean.domain.bookmark.entity.vo.Category.*;
import static com.meoguri.linkocean.domain.user.entity.vo.OAuthType.*;
import static org.assertj.core.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.meoguri.linkocean.domain.profile.command.service.dto.RegisterProfileCommand;
import com.meoguri.linkocean.domain.profile.command.service.dto.UpdateProfileCommand;
import com.meoguri.linkocean.domain.profile.query.service.dto.GetDetailedProfileResult;
import com.meoguri.linkocean.test.support.domain.service.BaseServiceTest;

@Transactional
class ProfileServiceImplTest extends BaseServiceTest {

	@Autowired
	private ProfileService profileService;

	private long profileId;

	@BeforeEach
	void setUp() {
		profileId = 사용자_프로필_동시_등록("haha@gmail.com", GOOGLE, "haha", IT);
	}

	@Test
	void 프로필_등록_성공() {
		//given
		long userId1 = 사용자_없으면_등록("user1@gmail.com", GOOGLE);
		final RegisterProfileCommand command = new RegisterProfileCommand(userId1, "crush", List.of(IT));

		//when
		final long profileId = profileService.registerProfile(command);

		//then
		assertThat(내_프로필_상세_조회(profileId)).isNotNull();
	}

	@Test
	void 프로필_등록_실패_사용자_이름_중복() {
		//given
		long userId1 = 사용자_없으면_등록("user1@gmail.com", GOOGLE);
		long userId2 = 사용자_없으면_등록("user2@gmail.com", GOOGLE);

		final String username = "duplicated";
		final RegisterProfileCommand command1 = new RegisterProfileCommand(userId1, username, List.of(IT));
		final RegisterProfileCommand command2 = new RegisterProfileCommand(userId2, username, List.of(IT));

		profileService.registerProfile(command1);

		//when then
		assertThatIllegalArgumentException()
			.isThrownBy(() -> profileService.registerProfile(command2));
	}

	@Test
	void 프로필_업데이트_성공() {
		//given
		final UpdateProfileCommand updateCommand = new UpdateProfileCommand(
			profileId, "papa", "updated image url", "updated bio", List.of(HUMANITIES, SCIENCE)
		);

		//when
		profileService.updateProfile(updateCommand);

		//then
		final GetDetailedProfileResult result = 내_프로필_상세_조회(profileId);
		assertThat(result.getUsername()).isEqualTo("papa");
		assertThat(result.getImage()).isEqualTo("updated image url");
		assertThat(result.getBio()).isEqualTo("updated bio");
		assertThat(result.getFavoriteCategories()).containsExactlyInAnyOrder(HUMANITIES, SCIENCE);
	}
}
