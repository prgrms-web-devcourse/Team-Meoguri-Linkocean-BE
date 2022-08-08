package com.meoguri.linkocean.configuration.resolver;

import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import com.meoguri.linkocean.domain.bookmark.entity.Bookmark;
import com.meoguri.linkocean.domain.bookmark.service.dto.FeedBookmarksSearchCond;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public final class GetBookmarkQueryParams {

	private final int page;
	private final int size;
	private final List<String> orderProperties;
	private final Bookmark.Category category;
	private final String searchTitle;

	private final boolean favorite;
	private final boolean follow;
	private final List<String> tags;

	public FeedBookmarksSearchCond toFeedSearchCond() {
		return new FeedBookmarksSearchCond(page, size, "upload", category.getKorName(), searchTitle, follow);
	}

	public Pageable toPageable() {
		// PageRequest 는 0 부터 페이지를 세기 때문에 조정해줌
		return PageRequest.of(page - 1, this.size, Sort.by(orderProperties.toArray(new String[0])));
	}
}
