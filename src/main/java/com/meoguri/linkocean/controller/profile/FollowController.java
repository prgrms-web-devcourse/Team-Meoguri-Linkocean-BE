package com.meoguri.linkocean.controller.profile;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.meoguri.linkocean.configuration.security.jwt.SecurityUser;
import com.meoguri.linkocean.domain.profile.service.command.FollowService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RequestMapping("/api/v1/profiles")
@RestController
public class FollowController {

	private final FollowService followService;

	/* 팔로우 */
	@PostMapping("/follow")
	public void follow(
		@AuthenticationPrincipal SecurityUser user,
		@RequestParam long followeeId
	) {
		followService.follow(user.getProfileId(), followeeId);
	}

	/* 언팔로우 */
	@PostMapping("/unfollow")
	public void unfollow(
		@AuthenticationPrincipal SecurityUser user,
		@RequestParam long followeeId
	) {
		followService.unfollow(user.getProfileId(), followeeId);
	}
}
