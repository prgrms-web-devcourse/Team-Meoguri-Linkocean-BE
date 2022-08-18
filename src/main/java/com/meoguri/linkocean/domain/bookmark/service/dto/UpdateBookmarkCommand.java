package com.meoguri.linkocean.domain.bookmark.service.dto;

import java.util.List;

import com.meoguri.linkocean.domain.bookmark.entity.vo.Category;
import com.meoguri.linkocean.domain.bookmark.entity.vo.OpenType;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public final class UpdateBookmarkCommand {

	private final long writerId;
	private final long bookmarkId;
	private final String title;
	private final String memo;
	private final Category category;
	private final OpenType openType;
	private final List<String> tagNames;
}
