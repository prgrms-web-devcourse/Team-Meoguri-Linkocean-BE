package com.meoguri.linkocean.controller.profile.dto;

import java.util.List;

import com.meoguri.linkocean.domain.profile.service.dto.UpdateProfileCommand;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public final class UpdateProfileRequest {
	private String username;
	private List<String> categories;
	private String bio;

	public UpdateProfileCommand toCommand(final long userId, final String image) {
		return new UpdateProfileCommand(userId, username, image, bio, categories);
	}

}
