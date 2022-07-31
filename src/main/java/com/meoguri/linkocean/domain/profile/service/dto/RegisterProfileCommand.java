package com.meoguri.linkocean.domain.profile.service.dto;

import java.util.List;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public final class RegisterProfileCommand {

	private final long userId;
	private final String username;
	private final List<String> categories;

}
