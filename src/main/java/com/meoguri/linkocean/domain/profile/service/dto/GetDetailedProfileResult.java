package com.meoguri.linkocean.domain.profile.service.dto;

import java.util.List;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public final class GetDetailedProfileResult {

	private final long profileId;
	private final String username;
	private final String image;
	private final String bio;
	private final List<String> favoriteCategories;
	private final boolean isFollow;
	private final int followerCount;
	private final int followeeCount;
}