package com.meoguri.linkocean.controller.profile.dto;

import java.util.List;

import com.meoguri.linkocean.domain.profile.service.dto.RegisterProfileCommand;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public final class CreateProfileRequest {

	private String username;
	private List<String> categories;

	public RegisterProfileCommand toCommand(final long userId) {
		return new RegisterProfileCommand(userId, username, categories);
	}

}
