package com.meoguri.linkocean.controller.profile;

import static com.meoguri.linkocean.controller.common.SimpleIdResponse.*;
import static java.util.stream.Collectors.*;

import java.util.List;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.meoguri.linkocean.configuration.security.jwt.SecurityUser;
import com.meoguri.linkocean.controller.common.SimpleIdResponse;
import com.meoguri.linkocean.controller.common.SliceResponse;
import com.meoguri.linkocean.controller.profile.dto.CreateProfileRequest;
import com.meoguri.linkocean.controller.profile.dto.GetMyProfileResponse;
import com.meoguri.linkocean.controller.profile.dto.GetProfilesResponse;
import com.meoguri.linkocean.controller.profile.support.GetProfileQueryParams;
import com.meoguri.linkocean.domain.bookmark.service.CategoryService;
import com.meoguri.linkocean.domain.bookmark.service.TagService;
import com.meoguri.linkocean.domain.profile.service.ProfileService;
import com.meoguri.linkocean.domain.profile.service.dto.SearchProfileResult;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1/profiles")
@RestController
public class ProfileController {

	private final ProfileService profileService;
	private final CategoryService categoryService;
	private final TagService tagService;

	@PostMapping
	public SimpleIdResponse createProfile(
		@AuthenticationPrincipal SecurityUser user,
		@RequestBody CreateProfileRequest request
	) {
		log.info("session user id {}", user.getId());
		return of(profileService.registerProfile(request.toCommand(user.getId())));
	}

	@GetMapping("/me")
	public GetMyProfileResponse getMyProfile(
		@AuthenticationPrincipal SecurityUser user
	) {
		return GetMyProfileResponse.of(
			profileService.getMyProfile(user.getId()),
			tagService.getMyTags(user.getId()),
			categoryService.getUsedCategories(user.getId())
		);
	}

	/* 프로필 목록 조회 - 머구리 찾기 */
	@GetMapping
	public SliceResponse<GetProfilesResponse> getProfiles(
		final @LoginUser SessionUser user,
		final GetProfileQueryParams queryParams
	) {
		final List<SearchProfileResult> results =
			profileService.searchProfilesByUsername(queryParams.toSearchCond(user.getId()));

		final List<GetProfilesResponse> response =
			results.stream().map(GetProfilesResponse::of).collect(toList());
		return SliceResponse.of("profiles", response);
	}
}
