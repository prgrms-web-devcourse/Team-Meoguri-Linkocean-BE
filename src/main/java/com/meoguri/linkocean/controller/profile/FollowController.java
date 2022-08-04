package com.meoguri.linkocean.controller.profile;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.meoguri.linkocean.configuration.security.oauth.LoginUser;
import com.meoguri.linkocean.configuration.security.oauth.SessionUser;
import com.meoguri.linkocean.domain.profile.service.FollowService;
import com.meoguri.linkocean.domain.profile.service.dto.FollowCommand;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RequestMapping("/api/v1/profiles")
@RestController
public class FollowController {

	private final FollowService followService;

	@PostMapping("/follow")
	public void follow(
		@LoginUser SessionUser user,
		@RequestParam long followeeId
	) {
		followService.follow(new FollowCommand(user.getId(), followeeId));
	}

	@PostMapping("/unfollow")
	public void unfollow(
		@LoginUser SessionUser user,
		@RequestParam long followeeId
	) {
		followService.unfollow(new FollowCommand(user.getId(), followeeId));
	}
}
