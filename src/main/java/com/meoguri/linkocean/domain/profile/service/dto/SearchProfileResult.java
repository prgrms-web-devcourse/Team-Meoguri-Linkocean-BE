package com.meoguri.linkocean.domain.profile.service.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class SearchProfileResult {

	private final long id;
	private final String username;
	private final String imageUrl;
	private final boolean isFollow;
}
