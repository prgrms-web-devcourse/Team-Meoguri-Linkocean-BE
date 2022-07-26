package com.meoguri.linkocean.controller.profile.dto;

import static java.util.stream.Collectors.*;

import java.util.List;

import com.meoguri.linkocean.internal.bookmark.entity.vo.Category;
import com.meoguri.linkocean.internal.profile.command.service.dto.RegisterProfileCommand;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public final class CreateProfileRequest {

	private String username;
	private List<String> categories;

	public RegisterProfileCommand toCommand(final long userId) {
		return new RegisterProfileCommand(
			userId,
			username,
			categories.stream().map(Category::of).collect(toList())
		);
	}
}
