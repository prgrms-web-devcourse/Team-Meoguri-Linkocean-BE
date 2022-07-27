package com.meoguri.linkocean.domain.profile.service.dto;

import java.util.List;
import java.util.stream.Collectors;

import lombok.Getter;

@Getter
public class RegisterProfileCommand {

	private final long userId;
	private final String username;
	private final List<String> categories;

	public RegisterProfileCommand(final long userId, final String username, final List<String> categories) {
		this.userId = userId;
		this.username = username;
		this.categories = categories.stream().map(String::toUpperCase).collect(Collectors.toList());
	}
}
