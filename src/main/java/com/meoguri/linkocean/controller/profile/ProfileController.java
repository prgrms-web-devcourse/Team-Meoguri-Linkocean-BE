package com.meoguri.linkocean.controller.profile;

import static java.util.stream.Collectors.*;

import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.meoguri.linkocean.configuration.security.jwt.SecurityUser;
import com.meoguri.linkocean.controller.common.SliceResponse;
import com.meoguri.linkocean.controller.profile.dto.CreateProfileRequest;
import com.meoguri.linkocean.controller.profile.dto.GetDetailedProfileResponse;
import com.meoguri.linkocean.controller.profile.dto.GetProfilesResponse;
import com.meoguri.linkocean.controller.profile.dto.UpdateProfileRequest;
import com.meoguri.linkocean.domain.bookmark.entity.vo.Category;
import com.meoguri.linkocean.domain.profile.persistence.query.dto.ProfileFindCond;
import com.meoguri.linkocean.domain.profile.service.command.ProfileService;
import com.meoguri.linkocean.domain.profile.service.query.CategoryQueryService;
import com.meoguri.linkocean.domain.profile.service.query.ProfileQueryService;
import com.meoguri.linkocean.domain.profile.service.query.dto.GetDetailedProfileResult;
import com.meoguri.linkocean.domain.profile.service.query.dto.GetProfileTagsResult;
import com.meoguri.linkocean.domain.profile.service.query.dto.GetProfilesResult;
import com.meoguri.linkocean.domain.tag.service.TagService;
import com.meoguri.linkocean.infrastructure.s3.S3Uploader;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1/profiles")
@RestController
public class ProfileController {

	private static final String PROFILES = "profiles";

	private final ProfileService profileService;
	private final ProfileQueryService profileQueryService;
	private final CategoryQueryService categoryQueryService;
	private final TagService tagService;

	private final S3Uploader s3Uploader;

	/* 프로필 등록 */
	@PostMapping
	public Map<String, Object> registerProfile(
		@AuthenticationPrincipal SecurityUser user,
		@RequestBody CreateProfileRequest request
	) {
		return Map.of("id", profileService.registerProfile(request.toCommand(user.getId())));
	}

	/* 내 프로필 조회 */
	@GetMapping("/me")
	public GetDetailedProfileResponse getMyProfile(
		@AuthenticationPrincipal SecurityUser user
	) {
		return getDetailedProfile(user, user.getProfileId());
	}

	/* 프로필 상세 조회 */
	@GetMapping("/{profileId}")
	public GetDetailedProfileResponse getDetailedProfile(
		final @AuthenticationPrincipal SecurityUser user,
		final @PathVariable long profileId
	) {
		// TODO - 얘 혼자 응답을 말아주는 로직이 컨트롤러에 위치하고 있음 서비스로 옮기던가 나중에 영속성에서 DTO 로 퍼올릴때 수정 할 것
		final GetDetailedProfileResult profile = profileQueryService.getByProfileId(user.getProfileId(), profileId);
		final List<GetProfileTagsResult> tags = tagService.getTags(profileId);
		final List<Category> categories = categoryQueryService.getUsedCategories(profileId);

		return GetDetailedProfileResponse.of(profile, tags, categories);
	}

	/* 프로필 수정 */
	@PutMapping("/me")
	public void updateProfile(
		@AuthenticationPrincipal SecurityUser user,
		@ModelAttribute UpdateProfileRequest request,
		@RequestPart(required = false, name = "image") MultipartFile profileImage
	) {
		final String imageUrl = s3Uploader.upload(profileImage, "profile");
		profileService.updateProfile(request.toCommand(user.getProfileId(), imageUrl));
	}

	/**
	 *  프로필 목록 조회 - 머구리 찾기
	 *  - username 은 필수다
	 */
	@GetMapping
	public SliceResponse<GetProfilesResponse> getProfiles(
		final @AuthenticationPrincipal SecurityUser user,
		final @RequestParam String username,
		final Pageable pageable
	) {
		final Slice<GetProfilesResult> results = profileQueryService.getProfiles(
			user.getProfileId(),
			ProfileFindCond.builder()
				.username(username)
				.build(),
			pageable
		);

		final List<GetProfilesResponse> response = results.stream().map(GetProfilesResponse::of).collect(toList());
		return SliceResponse.of(PROFILES, response, results.hasNext());
	}

	/**
	 * profileId 사용자의 팔로워 프로필 목록 조회
	 * 현재 접속 사용자의 팔로우 여부를 말아서 준다
	 */
	@GetMapping("/{profileId}/follower")
	public SliceResponse<GetProfilesResponse> getFollowerProfiles(
		final @AuthenticationPrincipal SecurityUser user,
		final @PathVariable long profileId,
		final Pageable pageable
	) {
		final Slice<GetProfilesResult> results = profileQueryService.getProfiles(
			user.getProfileId(),
			ProfileFindCond.builder()
				.profileId(profileId)
				.follower(true)
				.build(),
			pageable
		);

		final List<GetProfilesResponse> response = results.stream().map(GetProfilesResponse::of).collect(toList());
		return SliceResponse.of(PROFILES, response, results.hasNext());
	}

	/**
	 * profileId 사용자의 팔로이 프로필 목록 조회
	 * 현재 접속 사용자의 팔로우 여부를 말아서 준다
	 */
	@GetMapping("/{profileId}/followee")
	public SliceResponse<GetProfilesResponse> getFolloweeProfiles(
		final @AuthenticationPrincipal SecurityUser user,
		final @PathVariable long profileId,
		final Pageable pageable
	) {
		final Slice<GetProfilesResult> results = profileQueryService.getProfiles(
			user.getProfileId(),
			ProfileFindCond.builder()
				.profileId(profileId)
				.followee(true)
				.build(),
			pageable
		);

		final List<GetProfilesResponse> response = results.stream().map(GetProfilesResponse::of).collect(toList());
		return SliceResponse.of(PROFILES, response, results.hasNext());
	}

}
