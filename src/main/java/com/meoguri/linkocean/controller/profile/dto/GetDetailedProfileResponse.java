package com.meoguri.linkocean.controller.profile.dto;

import static java.util.stream.Collectors.*;

import java.util.List;

import com.meoguri.linkocean.internal.bookmark.entity.vo.Category;
import com.meoguri.linkocean.internal.bookmark.service.dto.GetUsedTagWithCountResult;
import com.meoguri.linkocean.internal.profile.query.service.dto.GetDetailedProfileResult;

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
		final List<GetUsedTagWithCountResult> tags,
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

	@Getter
	@NoArgsConstructor
	@AllArgsConstructor
	static final class GetProfileTagsResponse {

		private String tag;
		private long count;

		public static GetProfileTagsResponse of(final GetUsedTagWithCountResult result) {
			return new GetProfileTagsResponse(result.getTag(), result.getCount());
		}
	}

}
