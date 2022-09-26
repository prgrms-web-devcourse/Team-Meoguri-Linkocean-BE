package com.meoguri.linkocean.internal.profile.query.service.dto;

import java.util.List;

import com.meoguri.linkocean.internal.bookmark.entity.vo.Category;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public final class GetDetailedProfileResult {

	private final long profileId;
	private final String username;
	private final String image;
	private final String bio;
	private final List<Category> favoriteCategories;
	private final boolean isFollow;
	private final int followerCount;
	private final int followeeCount;
}
