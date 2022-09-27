package com.meoguri.linkocean.controller.bookmark.dto;

import static java.util.Collections.*;
import static java.util.Objects.*;

import java.util.List;

import com.meoguri.linkocean.internal.bookmark.entity.vo.Category;
import com.meoguri.linkocean.internal.bookmark.entity.vo.OpenType;
import com.meoguri.linkocean.internal.bookmark.service.dto.RegisterBookmarkCommand;

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

	public RegisterBookmarkCommand toCommand(final long profileId) {
		if (isNull(tags)) {
			tags = emptyList();
		}

		return new RegisterBookmarkCommand(profileId, url, title, memo, Category.of(category),
			OpenType.of(openType), tags);
	}
}
