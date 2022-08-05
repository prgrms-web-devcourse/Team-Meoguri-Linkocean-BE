package com.meoguri.linkocean.controller.profile.dto;

import static java.util.stream.Collectors.*;

import java.util.List;

import com.meoguri.linkocean.domain.profile.service.dto.GetMyProfileResult;
import com.meoguri.linkocean.domain.profile.service.dto.GetProfileTagsResult;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public final class GetMyProfileResponse {

	private Long profileId;
	private String imageUrl;
	private List<String> favoriteCategories;
	private String username;
	private String bio;
	private int followerCount;
	private int followeeCount;
	private List<GetProfileTagsResponse> tags;
	private List<String> categories;

	public static GetMyProfileResponse of(
		final GetMyProfileResult result,
		final List<GetProfileTagsResult> tags,
		final List<String> categories
	) {
		return new GetMyProfileResponse(
			result.getProfileId(),
			result.getImage(),
			result.getFavoriteCategories(),
			result.getUsername(),
			result.getBio(),
			result.getFollowerCount(),
			result.getFolloweeCount(),
			tags.stream().map(GetProfileTagsResponse::of).collect(toList()),
			categories
		);
	}

}
