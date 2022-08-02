package com.meoguri.linkocean.domain.profile.service.dto;

import java.util.List;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public final class GetMyProfileResult {

	private final long profileId;
	private final String username;
	private final String imageUrl;
	private final String bio;
	private final List<String> favoriteCategories;
	private final int followerCount;
	private final int followeeCount;
	private final boolean isFollow;
}
