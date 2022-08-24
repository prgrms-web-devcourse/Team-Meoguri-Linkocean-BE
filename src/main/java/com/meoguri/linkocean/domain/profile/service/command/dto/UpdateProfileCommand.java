package com.meoguri.linkocean.domain.profile.service.command.dto;

import java.util.List;

import com.meoguri.linkocean.domain.bookmark.entity.vo.Category;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public final class UpdateProfileCommand {

	private final long profileId;
	private final String username;
	private final String image;
	private final String bio;
	private final List<Category> categories;
}
