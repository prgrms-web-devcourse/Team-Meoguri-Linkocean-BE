package com.meoguri.linkocean.controller.profile;

import java.util.List;

import org.junit.jupiter.api.Test;

import com.meoguri.linkocean.controller.BaseControllerTest;

class FollowControllerTest extends BaseControllerTest {

	@Test
	void 팔로우_API_성공() throws Exception {

		유저_등록_로그인("haha@gmail.com", "GOOGLE");
		프로필_등록("haha", List.of("IT"));

		유저_등록_로그인("papa@gmail.com", "GOOGLE");
		프로필_등록("papa", List.of("자기계발"));

	}
}
