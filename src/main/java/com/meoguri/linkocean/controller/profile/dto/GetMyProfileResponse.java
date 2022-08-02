package com.meoguri.linkocean.controller.profile.dto;

import java.util.List;

import com.meoguri.linkocean.domain.bookmark.service.dto.GetMyTagsResult;
import com.meoguri.linkocean.domain.profile.service.dto.GetMyProfileResult;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class GetMyProfileResponse {

	private final Long profileId;
	private final String imageUrl;
	private final List<String> favoriteCategories;
	private final String username;
	private final String bio;
	private final int followerCount;
	private final int followeeCount;
	private final List<GetMyTagsResult> tags;
	private final List<String> categories;

	public static GetMyProfileResponse of(
		final GetMyProfileResult result,
		final List<GetMyTagsResult> tags,
		final List<String> categories) {
		return new GetMyProfileResponse(
			result.getProfileId(),
			result.getImageUrl(),
			result.getFavoriteCategories(),
			result.getUsername(),
			result.getBio(),
			result.getFollowerCount(),
			result.getFolloweeCount(),
			tags,
			categories
		);
	}

}
