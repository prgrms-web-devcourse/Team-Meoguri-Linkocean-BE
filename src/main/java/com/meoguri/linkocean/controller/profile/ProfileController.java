package com.meoguri.linkocean.controller.profile;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.meoguri.linkocean.configuration.security.oauth.LoginUser;
import com.meoguri.linkocean.configuration.security.oauth.SessionUser;
import com.meoguri.linkocean.controller.profile.dto.CreateProfileRequest;
import com.meoguri.linkocean.controller.profile.dto.GetMyProfileResponse;
import com.meoguri.linkocean.domain.bookmark.service.CategoryService;
import com.meoguri.linkocean.domain.bookmark.service.TagService;
import com.meoguri.linkocean.domain.profile.service.ProfileService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RequestMapping("/api/v1/profiles")
@RestController
public class ProfileController {

	private final ProfileService profileService;
	private final CategoryService categoryService;
	private final TagService tagService;

	@PostMapping
	public void createProfile(
		@LoginUser SessionUser user,
		@RequestBody CreateProfileRequest request
	) {
		profileService.registerProfile(request.toCommand(user.getId()));
	}

	@GetMapping("/me")
	public GetMyProfileResponse getMyProfile(
		@LoginUser SessionUser user
	) {
		return GetMyProfileResponse.of(
			profileService.getMyProfile(user.getId()),
			tagService.getMyTags(user.getId()),
			categoryService.getUsedCategories(user.getId())
		);
	}
}
