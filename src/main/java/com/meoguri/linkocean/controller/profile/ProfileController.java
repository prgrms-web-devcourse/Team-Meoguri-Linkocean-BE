package com.meoguri.linkocean.controller.profile;

import static com.meoguri.linkocean.controller.common.SimpleIdResponse.*;
import static java.util.stream.Collectors.*;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.meoguri.linkocean.configuration.resolver.GetProfileQueryParams;
import com.meoguri.linkocean.configuration.security.jwt.SecurityUser;
import com.meoguri.linkocean.controller.common.SimpleIdResponse;
import com.meoguri.linkocean.controller.common.SliceResponse;
import com.meoguri.linkocean.controller.profile.dto.CreateProfileRequest;
import com.meoguri.linkocean.controller.profile.dto.GetDetailedProfileResponse;
import com.meoguri.linkocean.controller.profile.dto.GetMyProfileResponse;
import com.meoguri.linkocean.controller.profile.dto.GetProfilesResponse;
import com.meoguri.linkocean.controller.profile.dto.UpdateProfileRequest;
import com.meoguri.linkocean.domain.bookmark.service.CategoryService;
import com.meoguri.linkocean.domain.profile.service.ProfileService;
import com.meoguri.linkocean.domain.profile.service.TagService;
import com.meoguri.linkocean.domain.profile.service.dto.GetDetailedProfileResult;
import com.meoguri.linkocean.domain.profile.service.dto.GetMyProfileResult;
import com.meoguri.linkocean.domain.profile.service.dto.GetProfileTagsResult;
import com.meoguri.linkocean.domain.profile.service.dto.ProfileSearchCond;
import com.meoguri.linkocean.domain.profile.service.dto.SearchProfileResult;
import com.meoguri.linkocean.infrastructure.s3.S3Uploader;

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
	private final S3Uploader s3Uploader;

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
		final GetMyProfileResult profile = profileService.getMyProfile(user.getId());
		final List<GetProfileTagsResult> tags = tagService.getMyTags(user.getId());
		final List<String> categories = categoryService.getUsedCategories(user.getId());

		return GetMyProfileResponse.of(profile, tags, categories);
	}

	@PostMapping("/me")
	public void updateMyProfile(
		@AuthenticationPrincipal SecurityUser user,
		@ModelAttribute UpdateProfileRequest request,
		@RequestPart(required = false) MultipartFile profilePhoto
	) {

		final String image = Optional.ofNullable(profilePhoto)
			.map(photo -> uploadImage(photo))
			.orElseGet(null);
		profileService.updateProfile(request.toCommand(user.getId(), image));

	}

	private String uploadImage(final MultipartFile photo) {
		try {
			return s3Uploader.upload(photo, "profile");
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	/* 프로필 목록 조회 - 머구리 찾기 */
	@GetMapping
	public SliceResponse<GetProfilesResponse> getProfiles(
		final @AuthenticationPrincipal SecurityUser user,
		final GetProfileQueryParams queryParams
	) {
		final List<SearchProfileResult> results =
			profileService.searchProfilesByUsername(queryParams.toSearchCond(user.getId()));

		final List<GetProfilesResponse> response = results.stream().map(GetProfilesResponse::of).collect(toList());
		return SliceResponse.of("profiles", response);
	}

	/**
	 * profileId 사용자의 팔로워 프로필 목록 조회
	 * 현재 접속 사용자의 팔로우 여부를 말아서 준다
	 */
	@GetMapping("/{profileId}/follower")
	public SliceResponse<GetProfilesResponse> getFollowerProfiles(
		final @AuthenticationPrincipal SecurityUser user,
		final @PathVariable long profileId,
		final GetProfileQueryParams queryParams
	) {
		final ProfileSearchCond cond = queryParams.toSearchCond(user.getId());
		final List<SearchProfileResult> results = profileService.searchFollowerProfiles(cond, profileId);

		final List<GetProfilesResponse> response = results.stream().map(GetProfilesResponse::of).collect(toList());
		return SliceResponse.of("profiles", response);
	}

	/**
	 * profileId 사용자의 팔로이 프로필 목록 조회
	 * 현재 접속 사용자의 팔로우 여부를 말아서 준다
	 */
	@GetMapping("/{profileId}/followee")
	public SliceResponse<GetProfilesResponse> getFolloweeProfiles(
		final @AuthenticationPrincipal SecurityUser user,
		final @PathVariable long profileId,
		final GetProfileQueryParams queryParams
	) {
		final ProfileSearchCond cond = queryParams.toSearchCond(user.getId());
		final List<SearchProfileResult> results = profileService.searchFolloweeProfiles(cond, profileId);

		final List<GetProfilesResponse> response = results.stream().map(GetProfilesResponse::of).collect(toList());
		return SliceResponse.of("profiles", response);
	}

	@GetMapping("/{profileId}")
	public GetDetailedProfileResponse getDetailedProfile(
		final @AuthenticationPrincipal SecurityUser user,
		final @PathVariable long profileId
	) {
		final GetDetailedProfileResult profile = profileService.getByProfileId(user.getId(), profileId);
		final List<GetProfileTagsResult> tags = tagService.getMyTags(user.getId());
		final List<String> categories = categoryService.getUsedCategories(user.getId());

		return GetDetailedProfileResponse.of(profile, tags, categories);
	}
}
