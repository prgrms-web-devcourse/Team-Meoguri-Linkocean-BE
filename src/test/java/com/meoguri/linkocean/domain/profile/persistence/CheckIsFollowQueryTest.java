package com.meoguri.linkocean.domain.profile.persistence;

import static com.meoguri.linkocean.domain.bookmark.entity.vo.Category.*;
import static com.meoguri.linkocean.domain.user.entity.vo.OAuthType.*;
import static java.util.List.of;
import static org.assertj.core.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;

import com.meoguri.linkocean.domain.profile.entity.Profile;
import com.meoguri.linkocean.test.support.persistence.BasePersistenceTest;

@Import(CheckIsFollowQuery.class)
class CheckIsFollowQueryTest extends BasePersistenceTest {

	@Autowired
	private CheckIsFollowQuery query;

	private Profile follower;
	private Profile followee;

	private long followerId;
	private long followeeId;

	@BeforeEach
	void setUp() {
		this.follower = 사용자_프로필_동시_저장_등록("follower@gmail.com", GOOGLE, "follower", IT);
		this.followee = 사용자_프로필_동시_저장_등록("followee@gmail.com", GOOGLE, "followee", IT);

		followerId = follower.getId();
		followeeId = followee.getId();
	}

	@Test
	void 팔로우_여부_체크_성공() {
		//given
		팔로우_저장(follower, followee);

		//when
		final boolean follow1 = query.isFollow(followerId, followee);
		final boolean follow2 = query.isFollow(followeeId, follower);

		//then
		assertThat(follow1).isEqualTo(true);
		assertThat(follow2).isEqualTo(false);
	}

	@Test
	void 팔로잉_여부_목록_조회_성공() {
		//given
		팔로우_저장(follower, followee);

		//when
		final List<Boolean> followeeIdsOfUser1 = query.isFollows(followerId, of(follower, followee));
		final List<Boolean> followeeIdsOfUser2 = query.isFollows(followeeId, of(follower, followee));

		//then
		assertThat(followeeIdsOfUser1).containsExactly(false, true);
		assertThat(followeeIdsOfUser2).containsExactly(false, false);
	}
}
