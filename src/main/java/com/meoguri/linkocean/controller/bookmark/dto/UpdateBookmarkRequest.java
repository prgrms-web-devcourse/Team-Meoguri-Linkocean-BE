package com.meoguri.linkocean.controller.bookmark.dto;

import java.util.List;

import com.meoguri.linkocean.internal.bookmark.entity.vo.Category;
import com.meoguri.linkocean.internal.bookmark.entity.vo.OpenType;
import com.meoguri.linkocean.internal.bookmark.service.dto.UpdateBookmarkCommand;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public final class UpdateBookmarkRequest {

	private String title;
	private String memo;
	private String category;
	private String openType;
	private List<String> tags;

	public UpdateBookmarkCommand toCommand(final long profileId, final long bookmarkId) {
		return new UpdateBookmarkCommand(profileId, bookmarkId, title, memo, Category.of(category),
			OpenType.of(openType), tags);
	}
}
