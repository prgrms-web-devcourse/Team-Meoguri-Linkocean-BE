package com.meoguri.linkocean.controller.profile.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.meoguri.linkocean.domain.profile.service.dto.GetMyProfileResult;
import com.meoguri.linkocean.domain.profile.service.dto.GetMyTagsResult;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class GetMyProfileResponse {

	private Long profileId;

	@JsonProperty("imageUrl")
	private String image;
	private List<String> favoriteCategories;
	private String username;
	private String bio;
	private int followerCount;
	private int followeeCount;
	private List<GetMyTagsResult> tags;
	private List<String> categories;

	public static GetMyProfileResponse of(
		final GetMyProfileResult result,
		final List<GetMyTagsResult> tags,
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
			tags,
			categories
		);
	}

}
