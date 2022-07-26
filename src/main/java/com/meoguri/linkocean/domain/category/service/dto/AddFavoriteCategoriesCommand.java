package com.meoguri.linkocean.domain.category.service.dto;

import java.util.List;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class AddFavoriteCategoriesCommand {

	private final long profileId;
	private final List<String> categoryNames;
}
