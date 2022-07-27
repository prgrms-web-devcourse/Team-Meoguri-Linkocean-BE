package com.meoguri.linkocean.domain.profile.service.dto;

import java.util.List;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class UpdateProfileCommand {

	private final long userID;
	private final String username;
	private final String imageUrl;
	private final String bio;
	private final List<String> categories;
}
