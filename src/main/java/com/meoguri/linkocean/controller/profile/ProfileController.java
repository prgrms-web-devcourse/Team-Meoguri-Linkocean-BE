package com.meoguri.linkocean.controller.profile;

import static com.meoguri.linkocean.controller.common.SimpleIdResponse.*;
import static java.util.stream.Collectors.*;

import java.util.List;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
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

	/* 프로필 등록 */
	@PostMapping
	public SimpleIdResponse createProfile(
		@AuthenticationPrincipal SecurityUser user,
		@RequestBody CreateProfileRequest request
	) {
		log.info("user id {}", user.getId());
		return of(profileService.registerProfile(request.toCommand(user.getId())));
	}

	/* 내 프로필 조회 */
	@GetMapping("/me")
	public GetMyProfileResponse getMyProfile(
		@AuthenticationPrincipal SecurityUser user
	) {
		final GetMyProfileResult profile = profileService.getMyProfile(user.getProfileId());
		final List<GetProfileTagsResult> tags = tagService.getTags(user.getProfileId());
		final List<String> categories = categoryService.getMyUsedCategories(user.getProfileId());

		return GetMyProfileResponse.of(profile, tags, categories);
	}

	/* 프로필 상세 조회 */
	@GetMapping("/{profileId}")
	public GetDetailedProfileResponse getDetailedProfile(
		final @AuthenticationPrincipal SecurityUser user,
		final @PathVariable long profileId
	) {
		final GetDetailedProfileResult profile = profileService.getByProfileId(user.getProfileId(), profileId);
		final List<GetProfileTagsResult> tags = tagService.getTags(profileId);
		final List<String> categories = categoryService.getUsedCategories(profileId);

		return GetDetailedProfileResponse.of(profile, tags, categories);
	}

	/* 내 프로필 수정 */
	@PutMapping("/me")
	public void updateMyProfile(
		@AuthenticationPrincipal SecurityUser user,
		@ModelAttribute UpdateProfileRequest request,
		@RequestPart(required = false, name = "image") MultipartFile profileImage
	) {
		final String imageUrl = s3Uploader.upload(profileImage, "profile");
		profileService.updateProfile(request.toCommand(user.getProfileId(), imageUrl));
	}

	/* 프로필 목록 조회 - 머구리 찾기 */
	@GetMapping
	public SliceResponse<GetProfilesResponse> getProfiles(
		final @AuthenticationPrincipal SecurityUser user,
		final GetProfileQueryParams queryParams
	) {
		final List<SearchProfileResult> results =
			profileService.searchProfilesByUsername(queryParams.toSearchCond(user.getProfileId()));

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
		final ProfileSearchCond cond = queryParams.toSearchCond(user.getProfileId());
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
		final ProfileSearchCond cond = queryParams.toSearchCond(user.getProfileId());
		final List<SearchProfileResult> results = profileService.searchFolloweeProfiles(cond, profileId);

		final List<GetProfilesResponse> response = results.stream().map(GetProfilesResponse::of).collect(toList());
		return SliceResponse.of("profiles", response);
	}

}
