package com.meoguri.linkocean.controller.profile.dto;

import static java.util.stream.Collectors.*;

import java.util.List;

import com.meoguri.linkocean.domain.bookmark.entity.vo.Category;
import com.meoguri.linkocean.domain.profile.query.service.dto.GetDetailedProfileResult;
import com.meoguri.linkocean.domain.profile.query.service.dto.GetProfileTagsResult;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public final class GetDetailedProfileResponse {

	private Long profileId;
	private String imageUrl;
	private List<String> favoriteCategories;
	private String username;
	private String bio;
	private Boolean isFollow;
	private int followerCount;
	private int followeeCount;
	private List<GetProfileTagsResponse> tags;
	private List<String> categories;

	public static GetDetailedProfileResponse of(
		final GetDetailedProfileResult result,
		final List<GetProfileTagsResult> tags,
		final List<Category> categories
	) {
		return new GetDetailedProfileResponse(
			result.getProfileId(),
			result.getImage(),
			result.getFavoriteCategories().stream().map(Category::getKorName).collect(toList()),
			result.getUsername(),
			result.getBio(),
			result.isFollow(),
			result.getFollowerCount(),
			result.getFolloweeCount(),
			tags.stream().map(GetProfileTagsResponse::of).collect(toList()),
			categories.stream().map(Category::getKorName).collect(toList())
		);
	}
}
