package com.meoguri.linkocean.domain.profile.command.service;

import static com.meoguri.linkocean.domain.bookmark.entity.vo.Category.*;
import static com.meoguri.linkocean.domain.user.entity.vo.OAuthType.*;
import static com.meoguri.linkocean.test.support.common.Assertions.*;
import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.meoguri.linkocean.test.support.service.BaseServiceTest;

class FollowServiceImplTest extends BaseServiceTest {

	@Autowired
	private FollowService followService;

	private long profileId1;
	private long profileId2;

	@BeforeEach
	void setUp() {
		profileId1 = 사용자_프로필_동시_등록("haha@gmail.com", GOOGLE, "haha", IT);
		profileId2 = 사용자_프로필_동시_등록("papa@gmail.com", GOOGLE, "papa", IT);
	}

	@Test
	void 팔로우_성공() {
		//when
		followService.follow(profileId1, profileId2);

		//then
		assertThat(내_프로필_상세_조회(profileId1).getFolloweeCount()).isEqualTo(1);
		assertThat(내_프로필_상세_조회(profileId2).getFollowerCount()).isEqualTo(1);
	}

	@Test
	void 팔로우_두번_요청_실패() {
		//given
		팔로우(profileId1, profileId2);

		//when then
		assertThatLinkoceanRuntimeException()
			.isThrownBy(() -> 팔로우(profileId1, profileId2));
	}

	@Test
	void 언팔로우_성공() {
		//given
		팔로우(profileId1, profileId2);

		//when
		followService.unfollow(profileId1, profileId2);

		//then
		assertThat(내_프로필_상세_조회(profileId1).getFolloweeCount()).isEqualTo(0);
		assertThat(내_프로필_상세_조회(profileId2).getFollowerCount()).isEqualTo(0);
	}

	@Test
	void 팔로우_한적이_없으면_언팔로우_실패() {
		//when then
		assertThatLinkoceanRuntimeException()
			.isThrownBy(() -> followService.unfollow(profileId1, profileId2));
	}
}
