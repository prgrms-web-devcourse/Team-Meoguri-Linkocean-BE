package com.meoguri.linkocean.controller.bookmark.dto;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import com.meoguri.linkocean.domain.bookmark.entity.vo.Category;
import com.meoguri.linkocean.domain.bookmark.entity.vo.OpenType;
import com.meoguri.linkocean.domain.bookmark.service.dto.RegisterBookmarkCommand;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public final class RegisterBookmarkRequest {

	private String url;
	private String title;
	private String memo;
	private String category;
	private String openType;
	private List<String> tags;

	public RegisterBookmarkCommand toCommand(final Long profileId) {
		tags = Optional.ofNullable(tags).orElseGet(Collections::emptyList);

		return new RegisterBookmarkCommand(profileId, url, title, memo, Category.of(category),
			OpenType.of(openType), tags);
	}
}
