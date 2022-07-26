package com.meoguri.linkocean.domain.profile.service.dto;

import java.util.List;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ProfileResult {

	private final long profileId;
	private final String imageUrl;
	private final List<String> categories;
	private final String username;
	private final String bio;
	private final int followerCount;
	private final int followeeCount;
	private final boolean isFollow;
}
