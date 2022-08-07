package com.meoguri.linkocean.controller.bookmark.dto;

import java.util.List;

import com.meoguri.linkocean.domain.bookmark.service.dto.UpdateBookmarkCommand;

import lombok.Getter;

@Getter
public final class UpdateBookmarkRequest {

	private String title;
	private String memo;
	private String category;
	private String openType;
	private List<String> tags;

	public UpdateBookmarkCommand toCommand(final long profileId, final long bookmarkId) {
		return new UpdateBookmarkCommand(profileId, bookmarkId, title, memo, category, openType, tags);
	}
}
