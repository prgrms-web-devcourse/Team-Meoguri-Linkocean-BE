package com.meoguri.linkocean.controller.profile.dto;

import static java.util.stream.Collectors.*;

import java.util.List;

import com.meoguri.linkocean.internal.bookmark.entity.vo.Category;
import com.meoguri.linkocean.internal.profile.command.service.dto.UpdateProfileCommand;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public final class UpdateProfileRequest {
	private String username;
	private List<String> categories;
	private String bio;

	public UpdateProfileCommand toCommand(final long profileId, final String image) {
		return new UpdateProfileCommand(
			profileId,
			username,
			image,
			bio,
			categories.stream().map(Category::of).collect(toList())
		);
	}

}
