package com.meoguri.linkocean.controller.bookmark.dto;

import java.util.List;

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

	public RegisterBookmarkCommand toCommand(final Long id) {
		return new RegisterBookmarkCommand(id, url, title, memo, category, openType, tags);
	}
}
