package com.meoguri.linkocean.internal.bookmark.service.dto;

import java.util.List;

import com.meoguri.linkocean.internal.bookmark.entity.vo.Category;
import com.meoguri.linkocean.internal.bookmark.entity.vo.OpenType;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public final class RegisterBookmarkCommand {

	private final long writerId;
	private final String url;
	private final String title;
	private final String memo;
	private final Category category;
	private final OpenType openType;
	private final List<String> tagNames;
}
